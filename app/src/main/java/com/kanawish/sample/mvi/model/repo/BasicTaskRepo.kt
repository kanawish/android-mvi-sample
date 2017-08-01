package com.kanawish.sample.mvi.model.repo

import com.kanawish.sample.mvi.intent.AppIntent
import com.kanawish.sample.mvi.model.TaskRepoState
import com.kanawish.sample.mvi.model.Task
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton

@Singleton
class BasicTaskRepo : TaskRepo {
    val intents = PublishSubject.create<AppIntent>()

    val store: Observable<TaskRepoState> = intents.scan(
            TaskRepoState(), { oldAppState, appIntent -> appIntent.invoke(oldAppState) })

    init {

    }

    override fun task(taskId: String): Observable<Task> {
        return store.map { it.tasks }
                .flatMap {
                    val filteredList = it.filter { it.id == taskId }
                    if( filteredList.count() == 1 ) {
                        Observable.just(it.single())
                    } else {
                        Observable.empty()
                    }
                }
    }

    override fun <T> taskAttribute(taskId: String, attributeMapper: (Task) -> T): Observable<T> {
        return store.map { it.tasks }
                .map { it.filter { it.id == taskId }.single() } // TODO: Error handling?
                .map(attributeMapper)
                .distinctUntilChanged()
    }

    override fun taskRepoState(): Observable<TaskRepoState> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun tasks(): Observable<List<Task>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun synchronizing(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    // ***** SINKS *****

    override fun process(appIntent: AppIntent) {
        intents.onNext(appIntent)
    }

}