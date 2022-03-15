package com.notes.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.notes.data.NoteDatabase
import com.notes.data.NoteRepoImpl
import com.notes.domain.NoteRepo
import com.notes.ui.details.NoteDetailsFragment
import com.notes.ui.list.NoteListFragment
import dagger.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
        ): AppComponent
    }

    fun getNoteRepo(): NoteRepo
}

@Module(
    includes = [
        AppModule.Binding::class
    ]
)
class AppModule {

    @Singleton
    @Provides
    fun provideNoteDatabase(
        context: Context
    ) = Room.databaseBuilder(
        context,
        NoteDatabase::class.java, "database-note.db"
    ).createFromAsset("database-note.db")
        .build()

    @Provides
    fun provideNoteDao(db: NoteDatabase) = db.noteDao()

    @Module
    interface Binding {

        @Binds
        fun bindContext(application: Application): Context

        @Binds
        fun bindNoteRepo(noteRepoImpl: NoteRepoImpl): NoteRepo
    }


}