package com.kanawish.sample.mvi.model

import com.jakewharton.rxrelay2.PublishRelay
import com.kanawish.sample.mvi.intent.Intent
import com.kanawish.sample.mvi.intent.Intent.CreateTask
import com.kanawish.sample.mvi.intent.Intent.DeleteTask
import com.kanawish.sample.mvi.intent.Intent.Refresh
import com.kanawish.sample.mvi.intent.Intent.UpdateTask
import com.kanawish.sample.mvi.model.SyncState.IDLE
import com.kanawish.sample.mvi.model.SyncState.PROCESS
import com.kanawish.sample.mvi.model.SyncState.PROCESS.Type.REFRESH
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import javax.inject.Singleton

@Singleton
class BasicModel : Model {

    companion object {
        private val INIT_STATE = ModelState(emptyList(), FilterType.ANY, IDLE)
    }

    // TODO: Filter Intent stream -> FilterType stream
    private val intents = PublishRelay.create<Intent>()

    private lateinit var storeDisposable: Disposable
    private val store: Observable<ModelState> = intents
            .toReducers()
            .observeOn(AndroidSchedulers.mainThread())
            .scan(INIT_STATE, { oldState, reducer -> reducer(oldState) })
            .replay(1)
            .apply { storeDisposable = connect() }

    private val restService: RestApi = retrofitBuilder().create(RestApi::class.java)

    init {
    }

    override fun syncState(): Observable<SyncState> = store.map(ModelState::syncState)

    override fun filter(): Observable<FilterType> = store.map(ModelState::filter)

    override fun tasks(filtered: Boolean): Observable<List<Task>> = store.map(ModelState::tasks)

    override fun task(taskId: String): Observable<Task> {
        return tasks().concatMap { tasks -> tasks.filter { it.id == taskId }.toObservable() }
    }

    // TODO: Error handling. (Ignoring would count as 'handling'...)
    // Example: An attributeMapper for description would be `Task::description`
    // The full call would be `model.taskAttribute(taskId, Task::description)`
    override fun <T> taskAttribute(taskId: String, attributeMapper: (Task) -> T): Observable<T> {
        return task(taskId)
                .map(attributeMapper)
                .distinctUntilChanged()
    }

    // ***** SINKS *****
    override fun accept(intent: Intent) = intents.accept(intent)

    /**
     * Reducer streams are allowed to emit on whatever thread they wish, but the
     * reducers themselves will be run on the main thread, and must be synchronous.
     */
    private fun Observable<Intent>.toReducers(): Observable<Reducer> {
        return concatMap { it ->
            when (it) {
                is Refresh -> refresh(it)
                is CreateTask -> create(it)
                is UpdateTask -> update(it)
                is DeleteTask -> delete(it)
            }
        }
    }

    /**
     *
     */
    private fun refresh(intent: Refresh): Observable<Reducer> {
        return Observable.create { e ->
            // Initial signal that the job started.
            e.onNext({ o -> o.copy(syncState = PROCESS(REFRESH)) })
            e.setDisposable(restService.tasksGet()
                    .subscribe(
                            // On success, flip state back to "IDLE" and emit the tasks.
                            { tasks ->
                                e.onNext { o -> o.copy(syncState = IDLE, tasks = tasks) }
                            },
                            // On failure, emit "ERROR" with associated throwable.
                            { error ->
                                e.onNext { o -> o.copy(syncState = SyncState.ERROR(error)) }
                            })
            )
        }
    }

    private fun create(intent: CreateTask): Observable<Reducer> = Observable.empty()

    private fun update(intent: UpdateTask): Observable<Reducer> = Observable.empty()

    private fun delete(intent: DeleteTask): Observable<Reducer> = Observable.empty()

}

typealias Reducer = (ModelState) -> ModelState
