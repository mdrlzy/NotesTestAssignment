package com.notes.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notes.di.RootScope
import com.notes.domain.Note
import com.notes.domain.NoteRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NoteDetailsViewModel(
    private val noteId: Long?,
    private val noteRepo: NoteRepo
) : ViewModel() {

    private val _note = MutableLiveData<Note>()
    val note: LiveData<Note> = _note

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (noteId != null) {
                _note.postValue(noteRepo.getById(noteId))
            } else {
                var newNote = Note(0, "", "", LocalDateTime.now(), LocalDateTime.now())
                newNote = noteRepo.save(newNote)
                _note.postValue(
                    newNote
                )
            }
        }
    }

    fun onTitleChanged(title: String) =
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            val note = _note.value ?: return@launch
            if (note.title == title)
                return@launch
            note.title = title
            note.modifiedAt = LocalDateTime.now()
            _note.postValue(note)
            noteRepo.save(note)
        }

    fun onDescriptionChanged(content: String) =
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            val note = _note.value ?: return@launch
            if (note.content == content)
                return@launch
            note.content = content
            note.modifiedAt = LocalDateTime.now()
            _note.postValue(note)
            noteRepo.save(note)
        }
}

class NoteDetailsViewModelFactory @AssistedInject constructor(
    @Assisted private val noteId: Long?,
    private val noteRepo: NoteRepo,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteDetailsViewModel(noteId, noteRepo) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted noteId: Long?): NoteDetailsViewModelFactory
    }
}