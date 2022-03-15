package com.notes.domain

import kotlinx.coroutines.flow.Flow

interface NoteRepo {
    fun getAll(): Flow<List<Note>>
    suspend fun getById(id: Long): Note
    suspend fun save(note: Note): Note
    suspend fun removeById(id: Long)
}