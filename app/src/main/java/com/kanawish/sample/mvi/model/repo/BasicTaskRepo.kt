package com.kanawish.sample.mvi.model.repo

import com.kanawish.sample.mvi.intent.AppIntent
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TaskRepoState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton

@Singleton
class BasicTaskRepo : TaskRepo {
    val intents = PublishSubject.create<AppIntent>()

    val store: Observable<TaskRepoState> = intents
            .scan(TaskRepoState(), { oldAppState, appIntent -> appIntent.invoke(oldAppState) })
            .observeOn(AndroidSchedulers.mainThread())

    init {

    }

    override fun task(taskId: String): Observable<Task> {
        return tasks()
                .flatMap {
                    val filteredList = it.filter { it.id == taskId }
                    if (filteredList.count() == 1) {
                        Observable.just(it.single())
                    } else {
                        Observable.empty()
                    }
                }
    }

    // TODO: Error handling. (Ignoring would count as 'handling'...)
    override fun <T> taskAttribute(taskId: String, attributeMapper: (Task) -> T): Observable<T> =
            tasks()
                    .map { it.filter { it.id == taskId }.single() }
                    .map(attributeMapper)
                    .distinctUntilChanged()

    override fun task(position: Int): Observable<Task> =
            tasks()
                    .map { it[position] }
                    .distinctUntilChanged()

    override fun <T> taskAttribute(position: Int, attributeMapper: (Task) -> T): Observable<T> =
            task(position)
                    .map(attributeMapper)
                    .distinctUntilChanged()

    override fun taskSource(id: String): TaskSource = object : TaskSource {
        override fun task(): Observable<Task> = task(id)
        override fun <T> attribute(attributeMapper: (Task) -> T): Observable<T> = taskAttribute(id, attributeMapper)
    }

    override fun taskSource(pos: Int): TaskSource = object : TaskSource {
        override fun task(): Observable<Task> = task(pos)
        override fun <T> attribute(attributeMapper: (Task) -> T): Observable<T> = taskAttribute(pos, attributeMapper)
    }

    interface TaskSource {
        fun task(): Observable<Task>
        fun <T> attribute(attributeMapper: (Task) -> T): Observable<T>
    }

    override fun taskRepoState(): Observable<TaskRepoState> = store

    override fun tasks(): Observable<List<Task>> = store.map(TaskRepoState::tasks)

    override fun synchronizing(): Observable<Boolean> = store.map(TaskRepoState::synchronizing)

    // ***** SINKS *****

    override fun process(appIntent: AppIntent) {
        intents.onNext(appIntent)
    }

}