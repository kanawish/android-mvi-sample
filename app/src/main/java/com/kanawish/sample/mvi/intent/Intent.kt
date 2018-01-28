package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.ClearCompletedTasksClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.FilterTypeSelected
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.RefreshTasksClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.TaskCheckBoxClick
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.ofType

/**
 * Intents for the Tasks Model
 */
sealed class Intent() {

    companion object {
        /**
         * For each view event groups (tasks, task, etc) define a transformer to map XYYViewEvent to Intent
         */
        val tasksViewEventTransformer = ObservableTransformer<TasksViewEvent, Intent> { upstream ->
            upstream
                    .map({
                        when (it) {
                            is TaskCheckBoxClick -> UpdateTask(oldTask = it.task, edit = updateCompleted(it.checked))
                            is FilterTypeSelected -> TODO()
                            ClearCompletedTasksClick -> TODO()
                            RefreshTasksClick -> TODO()
                            else -> Unit // Not all view events transform to AppIntents.
                        }
                    })
                    .ofType<Intent>() // Filters out 'Unit'
        }

        private fun updateCompleted(completed: Boolean): (Task) -> Task = { task -> task.copy(completed = completed) }
    }

    object Refresh : Intent()

    data class CreateTask(val title: String, val description: String) : Intent()

    data class UpdateTask(val oldTask: Task, val edit: (Task) -> Task) : Intent()

    data class DeleteTask(val task: Task) : Intent()

}
