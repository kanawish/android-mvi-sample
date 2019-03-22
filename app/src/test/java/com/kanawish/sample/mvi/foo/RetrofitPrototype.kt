package com.kanawish.sample.mvi.foo

import com.kanawish.sample.mvi.model.FilterType
import com.kanawish.sample.mvi.model.SyncState
import com.kanawish.sample.mvi.model.TasksState
import com.kanawish.sample.mvi.model.backend.TasksRestApi
import com.kanawish.sample.mvi.model.backend.TasksRestApiModule
import toothpick.Toothpick

/**
 * Android Studio lets us run arbitrary `main()` Kotlin functions from our `:App`
 * module classpath.
 *
 * Quite useful when you want a quick and dirty way to find out if a specific
 * piece of your code behaves as expected.
 *
 * Here is a quick prototype of how we could implement a "load tasks list"
 * Intent.
 */

fun main() {
    // Creates a bogus toothpick scope, to validate if our module setup works.
    val scope = Toothpick
        .openScope("MAIN")
        .apply { installModules(TasksRestApiModule) }

    // If everything is setup right, we should get a Retrofit implementation of our TaskRestApi interface.
    val tasksRestApi = scope.getInstance(TasksRestApi::class.java)

    // GET tasks returns an Observable<Map<String,Task>>
    val mockSequence = tasksRestApi.getTasks()
        .map<TasksState> { map ->
            TasksState(map.values.toList(), FilterType.ANY, SyncState.IDLE)
        }
        .onErrorReturn {
            TasksState(emptyList(),FilterType.ANY,SyncState.ERROR(it))
        }

    // In the real code, we'll build an Observable<Intent<T>>
    val disposable = mockSequence
        .toList() // Useful for this prototype, toList() makes our Observable a blocking one.
        .subscribe { resultsList ->
            resultsList.forEach { println(it) }
        }

    // Since it's a block chain, this will always be true below.
    println("disposable.isDisposed == ${disposable.isDisposed}")
}
