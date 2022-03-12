package com.notes.ui._base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias ViewBindingCreator<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

class ViewBindingStore<VB : ViewBinding>(
    private val viewBindingCreator: ViewBindingCreator<VB>
) {

    var viewBinding: VB? = null
        private set

    fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View = viewBindingCreator.invoke(
        inflater,
        container,
        false
    ).also {
        viewBinding = it
    }.root

    fun destroyViewBinding() {
        viewBinding = null
    }

}

abstract class ViewBindingFragment<VB : ViewBinding>(
    private val viewBindingCreator: ViewBindingCreator<VB>
) : Fragment() {

    private val viewBindingStore by lazy(LazyThreadSafetyMode.NONE) {
        ViewBindingStore(viewBindingCreator)
    }

    protected val viewBinding: VB?
        get() = viewBindingStore.viewBinding

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = viewBindingStore.createViewBinding(
        inflater,
        container
    )

    @CallSuper
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        viewBinding?.let {
            onViewBindingCreated(
                it,
                savedInstanceState
            )
        }
        super.onViewCreated(view, savedInstanceState)
    }

    open fun onViewBindingCreated(
        viewBinding: VB,
        savedInstanceState: Bundle?
    ) {
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        viewBindingStore.destroyViewBinding()
        onViewBindingDestroyed()
    }

    @CallSuper
    open fun onViewBindingDestroyed() {
    }

}