package com.notes.di

import com.notes.ui.details.NoteDetailsFragment
import com.notes.ui.list.NoteListFragment
import com.notes.ui.list.NoteListViewModel
import dagger.Component

@RootScope
@Component(
    dependencies = [
        AppComponent::class,
    ],
    modules = [
    ]
)
interface RootComponent {

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent
        ): RootComponent
    }

    fun inject(noteDetailsFragment: NoteDetailsFragment)
    fun inject(noteListFragment: NoteListFragment)

}