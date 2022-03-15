package com.notes.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notes.data.NoteDatabase
import com.notes.di.RootScope
import com.notes.domain.NoteRepo
import com.notes.ui._base.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class NoteListViewModel(
    private val noteRepo: NoteRepo
) : ViewModel() {

    private val _notes = MutableLiveData<List<NoteListItem>?>()
    val notes: LiveData<List<NoteListItem>?> = _notes

    private val _navigateToNoteCreation = MutableLiveData<Event<Long?>>()
    val navigateToNoteCreation: LiveData<Event<Long?>> = _navigateToNoteCreation

    init {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepo.getAll().collect { list ->
                _notes.postValue(
                    list.sortedByDescending {
                        it.modifiedAt
                    }.map {
                        NoteListItem(
                            id = it.id,
                            title = it.title,
                            content = it.content,
                        )
                    }
                )
            }

        }
    }

    fun onItemSwiped(pos: Int) {
        val notes = _notes.value?.toMutableList() ?: return
        val removedNote = notes.removeAt(pos)
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            noteRepo.removeById(removedNote.id)
        }
    }

    fun onItemClick(pos: Int) {
        _notes.value?.let {
            _navigateToNoteCreation.postValue(Event(it[pos].id))
        }
    }

    fun onCreateNoteClick() {
        _navigateToNoteCreation.postValue(Event(null))
    }

}

class NoteListViewModelFactory @Inject constructor(private val noteRepo: NoteRepo) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteListViewModel(noteRepo) as T
    }
}

data class NoteListItem(
    val id: Long,
    val title: String,
    val content: String,
)