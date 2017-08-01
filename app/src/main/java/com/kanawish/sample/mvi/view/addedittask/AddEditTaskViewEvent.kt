package com.kanawish.sample.mvi.view.addedittask

import io.reactivex.Observable
import io.reactivex.functions.Function3

/**
 * Created on 2017-07-10.
 */
sealed class AddEditTaskViewEvent {
    companion object {
        fun combine(titles: Observable<String>, descriptions: Observable<String>, clicks: Observable<Any>): Observable<SaveNewTaskClick> =
                Observable.combineLatest(titles, descriptions, clicks, Function3 { title, description, _ ->
                    SaveNewTaskClick(title, description)
                })

        fun combine(taskId:String, titles: Observable<String>, descriptions: Observable<String>, clicks: Observable<Any>): Observable<UpdateTaskClick> =
                Observable.combineLatest(titles, descriptions, clicks, Function3 { title, description, _ ->
                    UpdateTaskClick(taskId, title, description)
                })
    }

    data class SaveNewTaskClick(val title: String, val description: String) : AddEditTaskViewEvent()

    data class UpdateTaskClick(val taskId: String, val title: String, val description: String) : AddEditTaskViewEvent()
}