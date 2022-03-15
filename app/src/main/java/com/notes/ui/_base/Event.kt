package com.notes.ui._base

class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun handleContent() {
        hasBeenHandled = true
    }

    fun peekContent(): T = content
}