package com.notes.ui.details

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toolbar
import androidx.annotation.CheckResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.notes.databinding.FragmentNoteDetailsBinding
import com.notes.di.DependencyManager
import com.notes.ui._base.ViewBindingFragment
import com.notes.ui._base.textChanges
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class NoteDetailsFragment : ViewBindingFragment<FragmentNoteDetailsBinding>(
    FragmentNoteDetailsBinding::inflate
) {
    private val viewModel: NoteDetailsViewModel by viewModels {
        factory.create(arguments?.getLong(ID_KEY))
    }

    @Inject
    lateinit var factory: NoteDetailsViewModelFactory.Factory

    override fun onAttach(context: Context) {
        DependencyManager.inject(this)
        super.onAttach(context)
    }

    override fun onViewBindingCreated(
        viewBinding: FragmentNoteDetailsBinding,
        savedInstanceState: Bundle?
    ) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)
        viewModel.note.observe(
            viewLifecycleOwner,
            {
                if (it != null) {
                    if (it.title != viewBinding.etTitle.text.toString())
                        viewBinding.etTitle.setText(it.title)
                    if (it.content != viewBinding.etContent.text.toString())
                        viewBinding.etContent.setText(it.content)
                }
            }
        )

        viewBinding.etTitle.textChanges()
            .filterNot { it.isNullOrBlank() }
            .debounce(300)
            .onEach {
                viewModel.onTitleChanged(it.toString())
            }
            .launchIn(lifecycleScope)

        viewBinding.etContent.textChanges()
            .filterNot { it.isNullOrBlank() }
            .debounce(300)
            .onEach {
                viewModel.onDescriptionChanged(it.toString())
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        private const val ID_KEY = "noteId"

        fun newInstance(noteId: Long? = null) = NoteDetailsFragment().apply {
            noteId?.let {
                arguments = Bundle().apply {
                    putLong(ID_KEY, noteId)
                }
            }
        }
    }
}
