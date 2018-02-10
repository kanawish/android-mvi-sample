package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Model
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
import javax.inject.Inject
import javax.inject.Singleton

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

class InlineIntent(private val inline: (ModelState) -> ModelState) : ReducerIntent() {
    override fun invoke(oldState: ModelState): ModelState = inline(oldState)
}

class CreateTask(val title: String, val description: String) : ReducerIntent() {
    override fun invoke(oldState: ModelState): ModelState = TODO()
}

class UpdateTask(private val oldTask: Task, private val taskReducer: (Task) -> Task) : ReducerIntent() {
    override fun invoke(oldState: ModelState): ModelState {
        return oldState.copy(
                tasks = oldState.tasks.toMutableList()
                        .apply { set(indexOf(oldTask), taskReducer(oldTask)) }
                        .toList()
        )
    }
}

class ClearCompletedTasks() : ReducerIntent() {
    override fun invoke(oldState: ModelState): ModelState {
        return oldState.copy(
                tasks = oldState.tasks.filter { !it.completed }
        )
    }
}

/**
 * Useful to cut down on crufty `<TasksViewEvent>`.
 */
fun <T> Observable<T>.toViewEvent(mapper: (T) -> TasksViewEvent): Observable<TasksViewEvent> {
    return map<TasksViewEvent>(mapper)
}

/**
 * Maps TasksViewEvent to Intent
 */
fun map(event: TasksViewEvent): Intent {
    return when (event) {
        RefreshTasksPulled -> Refresh
        is FilterTypeSelected -> InlineIntent { oldState -> oldState.copy(filter = event.type) }
        is TaskCheckBoxClick -> UpdateTask(event.task) { task -> task.copy(completed = event.checked) }
        is TaskClick -> UpdateTask(event.task) { task -> task.copy(completed = !task.completed) }
        ClearCompletedTasksClick -> ClearCompletedTasks()
    }
}

@Singleton
class IntentMapper @Inject constructor(val model: Model) {
    /**
     * Accepts View Events, converts them, and passes the resulting Intents
     * to the Model.
     */
    fun accept(event: TasksViewEvent) {
        model.accept(map(event))
    }
}