package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.ClearCompletedTasksClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.FilterTypeSelected
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.RefreshTasksPulled
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.TaskCheckBoxClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.TaskClick
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType

/**
 * Intents for the Tasks Model
 */
sealed class Intent() {

    companion object {
        /**
         * For each view event groups (tasks, task, etc) define a transformer to map XYYViewEvent to Intent
         */
        fun updateCompleted(completed: Boolean): (Task) -> Task = { task -> task.copy(completed = completed) }
    }

    object Refresh : Intent()

    data class CreateTask(val title: String, val description: String) : Intent()

    data class UpdateTask(val oldTask: Task, val edit: (Task) -> Task) : Intent()

    data class DeleteTask(val task: Task) : Intent()

}

fun Observable<TasksViewEvent>.toIntent(): Observable<Intent> {
    return this
            .map { tasksViewEvent ->
                when (tasksViewEvent) {
                    RefreshTasksPulled -> Intent.Refresh
                    is FilterTypeSelected -> TODO()
                    is TaskCheckBoxClick -> Intent.UpdateTask(oldTask = tasksViewEvent.task, edit = Intent.updateCompleted(tasksViewEvent.checked))
                    is TaskClick -> TODO()
                    ClearCompletedTasksClick -> TODO()
                }
            }
            .ofType() // Filters out 'Unit'
}
