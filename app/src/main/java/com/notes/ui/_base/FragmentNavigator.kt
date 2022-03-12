package com.notes.ui._base

import androidx.fragment.app.Fragment

interface FragmentNavigator {
    fun navigateTo(
        fragment: Fragment
    )
}