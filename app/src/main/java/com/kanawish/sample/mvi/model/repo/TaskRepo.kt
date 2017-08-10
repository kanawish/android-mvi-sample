package com.kanawish.sample.mvi.model.repo

import com.kanawish.sample.mvi.intent.AppIntent
import com.kanawish.sample.mvi.model.TaskRepoState
import com.kanawish.sample.mvi.model.Task
import io.reactivex.Observable


/**
 * Created on 2017-05-26.
 */
interface TaskRepo {

    // ***** SOURCES *****

    /**
     * stream: All app state changes
     */
    fun taskRepoState(): Observable<TaskRepoState>

    /**
     * This observable emits the complete list of tasks every time changes occur to it.
     */
    fun tasks(): Observable<List<Task>>

    // TODO: Add observables that allow tracking specific changes (add/remove/updates) to the list.

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

    fun task(position: Int): Observable<Task>

    fun <T> taskAttribute(position: Int, attributeMapper: (Task) -> T): Observable<T>

    fun taskSource(id: String): BasicTaskRepo.TaskSource

    fun taskSource(pos: Int): BasicTaskRepo.TaskSource

    /**
     * This returns an observable that reflects if the task repo is currently synchronizing.
     *
     * The idea is that UIs sometimes need to reflect these states to the user, by showing a
     * spinner or disable certain affordances that would be unresponsive right now, etc.
     */
    fun synchronizing(): Observable<Boolean>


    // ***** SINKS *****

    fun process(appIntent: AppIntent)

}


