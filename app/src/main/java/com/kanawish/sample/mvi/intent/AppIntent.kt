package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.TaskRepoState
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.*
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.ofType

/**
 * Created on 2017-06-06.
 */
sealed class AppIntent : (TaskRepoState) -> TaskRepoState {

    companion object {
        val tasksViewEventTransformer = ObservableTransformer<TasksViewEvent, AppIntent> { upstream ->
            upstream
                    .map({
                        when (it) {
                            is TaskCheckBoxClick -> UpdateTask(oldTask = it.task, edit = toggleCompleted)
                            is FilterTypeSelected -> TODO()
                            ClearCompletedTasksClick -> TODO()
                            RefreshTasksClick -> TODO()
                            else -> Unit // Not all view events transform to AppIntents.
                        }
                    })
                    .ofType<AppIntent>() // Filters out 'Unit'
        }

        private val toggleCompleted: (Task) -> Task = { task -> task.copy(completed = !task.completed) }
    }

    // Task List Operations

    class CreateTask(val title: String, val description: String) : AppIntent() {
        override fun invoke(old: TaskRepoState): TaskRepoState =
                old.copy(tasks = old.tasks.copy { add(Task(title = title, description = description)) })
    }

    class UpdateTask(val oldTask: Task, val edit: (Task) -> Task) : AppIntent() {
        override fun invoke(old: TaskRepoState): TaskRepoState =
                old.copy(tasks = old.tasks.copy {
                    if (contains(oldTask)) set(indexOf(oldTask), edit.invoke(oldTask))
                })
    }

    class DeleteTask(val task: Task) : AppIntent() {
        override fun invoke(old: TaskRepoState): TaskRepoState =
                old.copy(tasks = old.tasks.copy { remove(task) })
    }

}
