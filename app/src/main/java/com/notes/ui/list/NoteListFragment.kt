package com.notes.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.notes.databinding.FragmentNoteListBinding
import com.notes.databinding.ListItemNoteBinding
import com.notes.di.DependencyManager
import com.notes.ui._base.FragmentNavigator
import com.notes.ui._base.ViewBindingFragment
import com.notes.ui._base.findImplementationOrThrow
import com.notes.ui.details.NoteDetailsFragment
import com.notes.ui.details.NoteDetailsViewModel
import com.notes.ui.details.NoteDetailsViewModelFactory
import javax.inject.Inject

class NoteListFragment : ViewBindingFragment<FragmentNoteListBinding>(
    FragmentNoteListBinding::inflate
) {

    @Inject
    lateinit var factory: NoteListViewModelFactory

    private val viewModel: NoteListViewModel by viewModels {
        factory
    }

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onAttach(context: Context) {
        DependencyManager.inject(this)
        super.onAttach(context)
    }

    override fun onViewBindingCreated(
        viewBinding: FragmentNoteListBinding,
        savedInstanceState: Bundle?
    ) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)

        recyclerViewAdapter = RecyclerViewAdapter(viewModel)
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or
                    ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                viewModel.onItemSwiped(viewHolder.adapterPosition)
            }
        }
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(viewBinding.list)
        viewBinding.list.adapter = recyclerViewAdapter
        viewBinding.list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayout.VERTICAL
            )
        )
        viewBinding.createNoteButton.setOnClickListener {
            viewModel.onCreateNoteClick()
        }

        viewModel.notes.observe(
            viewLifecycleOwner,
            {
                if (it != null) {
                    recyclerViewAdapter.setItems(it)
                }
            }
        )
        viewModel.navigateToNoteCreation.observe(
            viewLifecycleOwner,
            {
                if (!it.hasBeenHandled) {
                    it.handleContent()
                    findImplementationOrThrow<FragmentNavigator>()
                        .navigateTo(
                            NoteDetailsFragment.newInstance(it.peekContent())
                        )
                }
            }
        )
    }

    private class RecyclerViewAdapter(private val viewModel: NoteListViewModel) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        private val items = mutableListOf<NoteListItem>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) = ViewHolder(
            ListItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            viewModel
        )

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size

        fun setItems(
            newItems: List<NoteListItem>
        ) {
            val result = DiffUtil.calculateDiff(NoteItemDiffUtil(items, newItems))
            items.clear()
            items.addAll(newItems)
            result.dispatchUpdatesTo(this)
        }

        private class ViewHolder(
            private val binding: ListItemNoteBinding,
            private val viewModel: NoteListViewModel
        ) : RecyclerView.ViewHolder(
            binding.root
        ) {
            fun bind(
                note: NoteListItem
            ) {
                binding.titleLabel.text = note.title
                binding.contentLabel.text = note.content
                binding.root.setOnClickListener {
                    viewModel.onItemClick(adapterPosition)
                }
            }
        }
    }

    private class NoteItemDiffUtil(
        private val oldList: List<NoteListItem>,
        private val newList: List<NoteListItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean = oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean = oldList[oldItemPosition] == newList[newItemPosition]
    }

}