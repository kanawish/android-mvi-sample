package com.kanawish.sample.mvi.model

import com.jakewharton.rxrelay2.PublishRelay
import com.kanawish.sample.mvi.intent.Intent
import com.kanawish.sample.mvi.intent.ObservableIntent
import com.kanawish.sample.mvi.intent.ReducerIntent
import com.kanawish.sample.mvi.model.SyncState.IDLE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import timber.log.Timber
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
            .doOnNext { Timber.i("STORE STATE - ${it.syncState} / ${it.tasks.size} items") }
            .doOnError { Timber.i("store.doOnError()") }
            .doOnComplete { Timber.i("store.doOnComplete() called") }
            .replay(1)
            .apply { storeDisposable = connect() }

    override fun syncState(): Observable<SyncState> = store.map(ModelState::syncState)

    override fun filter(): Observable<FilterType> = store.map(ModelState::filter)

    override fun tasks(filtered: Boolean): Observable<List<Task>> {
        return if (filtered)
            filteredTasks()
        else
            store.map(ModelState::tasks).distinctUntilChanged()
    }

    private fun filteredTasks(): Observable<List<Task>> {
        return store
                .map { it.tasks.filter(it.filter.predicate) }
                .distinctUntilChanged()
    }

    override fun task(taskId: String): Observable<Task> {
        return store.map(ModelState::tasks)
                .concatMap { tasks -> tasks.filter { it.id == taskId }.toObservable() }
                .distinctUntilChanged()
    }

    // TODO: Better error handling. (Ignoring could count as 'handling' I guess, but meh...)
    // Example: An attributeMapper for description would be `Task::description`
    // The full call would be `model.taskAttribute(taskId, Task::description)`
    override fun <T> taskAttribute(taskId: String, attributeMapper: (Task) -> T): Observable<T> {
        return task(taskId)
                .map(attributeMapper)
                .distinctUntilChanged()
    }

    /**
     * Reducer streams are allowed to emit on whatever thread they wish, but the
     * reducers themselves will be run on the main thread, and must be synchronous.
     */
    private fun Observable<Intent>.toReducers(): Observable<Reducer> {
        return this.concatMap { intent ->
            when (intent) {
                is ReducerIntent -> Observable.just(intent)
                is ObservableIntent -> intent()
            }
        }
    }

    // ***** SINKS *****
    override fun accept(intent: Intent) = intents.accept(intent)

}


typealias Reducer = (ModelState) -> ModelState