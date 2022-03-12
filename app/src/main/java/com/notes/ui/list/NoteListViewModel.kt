package com.notes.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.data.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoteListViewModel @Inject constructor(
    private val noteDatabase: NoteDatabase
) : ViewModel() {

    private val _notes = MutableLiveData<List<NoteListItem>?>()
    val notes: LiveData<List<NoteListItem>?> = _notes

    private val _navigateToNoteCreation = MutableLiveData<Unit?>()
    val navigateToNoteCreation: LiveData<Unit?> = _navigateToNoteCreation

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _notes.postValue(
                noteDatabase.noteDao().getAll().map {
                    NoteListItem(
                        id = it.id,
                        title = it.title,
                        content = it.content,
                    )
                }
            )
        }
    }

    fun onCreateNoteClick() {
        _navigateToNoteCreation.postValue(Unit)
    }

}

data class NoteListItem(
    val id: Long,
    val title: String,
    val content: String,
)