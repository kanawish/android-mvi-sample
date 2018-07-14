package com.kanawish.sample.mvi

import android.app.Application
import com.kanawish.sample.mvi.di.ToothpickActivityLifecycleCallbacks
import com.kanawish.sample.mvi.model.backend.TasksRestApiModule
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieApplicationModule

/**
 * TaskApp sets up Application level concerns:
 * - Timber for better log management.
 * - Toothpick for DI.
 *
 * DI scopes are:
 * Application[ Activity [ Fragment ] ]
 *
 * Activity scope lifecycle: from `onActivityCreated()` to `onActivityDestroyed()`
 * Fragment scope lifecycle: from `onFragmentPreAttached()` to `onFragmentDetached()`
 */
class TasksApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Logger init
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.i("%s %d", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        // DI Root Scope init
        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(ToothpickActivityLifecycleCallbacks())
    }

    /**
     * A very basic Application scope.
     */
    private fun openApplicationScope(app: Application): Scope {
        return Toothpick.openScope(app).apply {
            installModules(
                    SmoothieApplicationModule(app),
                    TasksRestApiModule
            )
        }
    }

}