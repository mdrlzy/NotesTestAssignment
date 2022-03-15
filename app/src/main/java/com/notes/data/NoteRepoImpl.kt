package com.notes.data

import com.notes.domain.Note
import com.notes.domain.NoteRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepoImpl @Inject constructor(private val dao: NoteDao) : NoteRepo {
    override fun getAll(): Flow<List<Note>> = dao.getAll().map { list ->
        list.map { it.toNote() }
    }

    override suspend fun getById(id: Long): Note = dao.findById(id)!!.toNote()

    override suspend fun save(note: Note): Note {
        val id = dao.insert(note.toDbo())
        return note.copy(id = id)
    }

    override suspend fun removeById(id: Long) {
        dao.deleteById(id)
    }
}

private fun Note.toDbo(): NoteDbo =
    NoteDbo(
        id,
        title,
        content,
        createdAt,
        modifiedAt
    )

private fun NoteDbo.toNote(): Note =
    Note(
        id,
        title,
        content,
        createdAt,
        modifiedAt
    )
