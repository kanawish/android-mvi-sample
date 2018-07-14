package com.kanawish.sample.mvi.di

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks
import toothpick.Toothpick

/**
 * Lifecycle callback that:
 * - `onFragmentPreAttached` will and inject the fragment in a fragment sub-scope, attached to activity (or parent context).
 * - `onFragmentDetached` will close the sub-scope.
 */
class ToothpickFragmentLifecycleCallbacks() : FragmentLifecycleCallbacks() {
    override fun onFragmentPreAttached(fm: FragmentManager?, f: Fragment?, context: Context?) {
        f?.let { fragment -> Toothpick.inject(fragment, Toothpick.openScopes(context, fragment)) }
    }

    override fun onFragmentDetached(fm: FragmentManager?, f: Fragment?) {
        f?.let { fragment -> Toothpick.closeScope(fragment) }
    }
}