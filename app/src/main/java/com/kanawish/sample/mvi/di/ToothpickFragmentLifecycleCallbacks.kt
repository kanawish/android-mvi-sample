package com.kanawish.sample.mvi.di

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import toothpick.Toothpick

/**
 * Lifecycle callback that:
 * - `onFragmentPreAttached` will and inject the fragment in a fragment sub-scope, attached to activity (or parent context).
 * - `onFragmentDetached` will close the sub-scope.
 */
class ToothpickFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentPreAttached(fm: FragmentManager, fragment: Fragment, context: Context) {
        Toothpick.inject(fragment, Toothpick.openScopes(context, fragment))
    }

    override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
        Toothpick.closeScope(fragment)
    }
}