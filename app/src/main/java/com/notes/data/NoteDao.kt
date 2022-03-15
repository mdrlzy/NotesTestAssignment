package com.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<NoteDbo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg notes: NoteDbo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteDbo): Long

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): NoteDbo?

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}