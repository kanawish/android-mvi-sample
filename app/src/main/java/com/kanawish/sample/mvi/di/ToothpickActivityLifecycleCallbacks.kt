package com.kanawish.sample.mvi.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule

/**
 * Auto-injects Activities at the right time in their lifecycle.
 *
 * Also, sets up auto-injection for child fragments.
 */
class ToothpickActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    private val fragmentCallbacks = ToothpickFragmentLifecycleCallbacks()

    override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
        activity?.apply {
            Toothpick.inject(this, openActivityScope(this))
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, true)
        }
    }

    private fun openActivityScope(activity: Activity): Scope {
        return Toothpick.openScopes(activity.application, activity).apply {
            installModules(
                    SmoothieActivityModule(activity),
                    ActivityModule
            )
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
        }

        activity?.apply {
            Toothpick.closeScope(this)
        }
    }

    // Unused, moved to bottom of class for readability.
    override fun onActivityStarted(activity: Activity?) {}
    override fun onActivityPaused(activity: Activity?) {}
    override fun onActivityResumed(activity: Activity?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {}

}