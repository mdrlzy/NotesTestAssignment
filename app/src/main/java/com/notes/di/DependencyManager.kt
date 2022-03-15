package com.notes.di

import android.app.Application
import com.notes.ui.details.NoteDetailsFragment
import com.notes.ui.list.NoteListFragment

class DependencyManager private constructor(
    application: Application
) {

    companion object {
        private lateinit var instance: DependencyManager

        fun init(application: Application) {
            instance = DependencyManager(application)
        }

        fun inject(noteDetailsFragment: NoteDetailsFragment) =
            instance.rootComponent.inject(noteDetailsFragment)

        fun inject(noteListFragment: NoteListFragment) =
            instance.rootComponent.inject(noteListFragment)
    }

    private val appComponent: AppComponent =
        DaggerAppComponent.factory().create(application)

    private val rootComponent: RootComponent =
        DaggerRootComponent.factory().create(appComponent)

}
