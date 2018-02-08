package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.ModelState
import com.kanawish.sample.mvi.model.Reducer
import com.kanawish.sample.mvi.model.RestApi
import com.kanawish.sample.mvi.model.SyncState
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.retrofitBuilder
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.ClearCompletedTasksClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.FilterTypeSelected
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.RefreshTasksPulled
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.TaskCheckBoxClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.TaskClick
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Intents for the Tasks Model
 *
 * Intent is a component whose sole responsibility is to translate user input events into
 * model-friendly events. It should interpret what the user is trying to do in terms of
 * model updates, and export these ‘user intentions’ as events.
 * – Andre Medeiros
 *
 */

sealed class Intent

abstract class ReducerIntent : Intent(), Reducer
abstract class ObservableIntent : Intent(), () -> Observable<Reducer>

object Refresh : ObservableIntent() {
    override fun invoke(): Observable<Reducer> = Observable.create { e ->
        // Initial signal that the job started.
        e.onNext({ o -> o.copy(syncState = SyncState.PROCESS(SyncState.PROCESS.Type.REFRESH)) })
        e.setDisposable(restService.tasksGet()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        // On success, flip state back to "IDLE" and emit the tasks.
                        { tasks ->
                            e.onNext { o -> o.copy(syncState = SyncState.IDLE, tasks = tasks) }
                            e.onComplete()
                        },
                        // On failure, emit "ERROR" with associated throwable.
                        { error ->
                            Timber.e(error)
                            e.onNext { o -> o.copy(syncState = SyncState.ERROR(error)) }
                            e.onNext { o -> o.copy(syncState = SyncState.IDLE) }
                            e.onComplete()
                        })
        )
    }

    private val restService: RestApi = retrofitBuilder().create(RestApi::class.java)
}

data class CreateTask(val title: String, val description: String) : ReducerIntent() {
    override fun invoke(oldState: ModelState): ModelState = TODO()
}

data class UpdateTask(val oldTask: Task, val taskReducer: (Task) -> Task) : ReducerIntent() {
    override fun invoke(oldModel: ModelState): ModelState {
        return oldModel.copy(
                tasks = oldModel.tasks
                        .toMutableList()
                        .apply { set(indexOf(oldTask), taskReducer(oldTask)) }
                        .toList()
        )
    }
}

data class DeleteTask(val task: Task) : ReducerIntent() {
    override fun invoke(oldModel: ModelState): ModelState = TODO()
}

fun Observable<TasksViewEvent>.toIntent(): Observable<Intent> {
    return this
            .map { tasksViewEvent ->
                when (tasksViewEvent) {
                    RefreshTasksPulled -> Refresh as Intent
                    is FilterTypeSelected -> TODO()
                    is TaskCheckBoxClick -> UpdateTask(
                            oldTask = tasksViewEvent.task,
                            taskReducer = { task -> task.copy(completed = tasksViewEvent.checked) }
                    )
                    is TaskClick -> UpdateTask(
                            oldTask = tasksViewEvent.task,
                            taskReducer = { task -> task.copy(completed = !task.completed) }
                    )
                    ClearCompletedTasksClick -> TODO()
                    TasksViewEvent.ClearCompletedTasksClick -> TODO()
                }
            }
//            .ofType() // Filters out 'Unit'
}
