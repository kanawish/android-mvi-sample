package com.kanawish.sample.mvi.model

import com.kanawish.sample.mvi.intent.Intent
import io.reactivex.Observable


/**
 * Created on 2017-05-26.
 */
interface Model {

    // ***** SOURCES *****

    /**
     * This returns an observable that reflects the state of background synchronization processes.
     *
     * The idea is that UIs sometimes need to reflect these states to the user, by showing a
     * spinner or disabling certain UI elements.
     */
    fun syncState(): Observable<SyncState>

    /**
     * Current filter type stream. [BONUS]
     */
    fun filter(): Observable<FilterType>

    /**
     * This observable emits the complete list of tasks every time changes occur to it.
     */
    fun tasks(filtered: Boolean = true): Observable<List<Task>>

    /**
     * This returns an observable that tracks changes to the task matching the provided Id.
     *
     * When this task is no longer found in the task repo, the observable will complete.
     */
    fun task(taskId: String): Observable<Task>

    /**
     * This returns an observable that tracks changes to a specific task attribute.
     *
     * @param taskId id of the task
     * @param attributeMapper the task attribute we are interested in.
     */
    fun <T> taskAttribute(taskId: String, attributeMapper: (Task) -> T): Observable<T>

    // ***** SINKS *****

    fun accept(intent: Intent)

}


