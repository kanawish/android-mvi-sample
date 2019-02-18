package com.kanawish.sample.mvi.utils

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A testing rule allowing us to replace the main-thread scheduler. This is needed to unit test
 * rx chains that use the main thread.
 */
class ReplaceMainThreadSchedulerRule(val replacementScheduler: Scheduler = Schedulers.trampoline()) : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { replacementScheduler }
                try {
                    base.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                }
            }
        }
    }
}