package com.notes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(
    entities = [
        NoteDbo::class
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(
    LocalDateTimeConverter::class
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

class LocalDateTimeConverter {

    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun stringToLocalDateTime(
        value: String
    ): LocalDateTime {
        return LocalDateTime.parse(value, dateTimeFormatter)
    }

    @TypeConverter
    fun localDateTimeToString(
        localDateTime: LocalDateTime
    ): String {
        return dateTimeFormatter.format(localDateTime)
    }


}