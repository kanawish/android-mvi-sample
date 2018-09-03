package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.FilterType
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TasksModelState
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent
import io.reactivex.Observable

/**
 TODO - Exercise for reader: Try to improve the code below, using your own List<T> extension function.

 Hint: see `ListUtils.kt` file in this project.
*/

fun saveTaskIntent(task: Task): Intent<TasksModelState> = intent {
    tasks.toMutableList().let { newList ->
        newList.find { task.id == it.id }?.let {
            newList[newList.indexOf(it)] = task
        } ?: newList.add(task)

        copy(tasks = newList)
    }
}

fun addNewTask(newTask: Task): Intent<TasksModelState> {
    return intent {
        tasks.toMutableList().let { newList ->
            newList.add(newTask)
            copy(tasks = newList)
        }
    }
}

fun deleteTaskIntent(taskId: String): Intent<TasksModelState> {
    return intent {
        copy(tasks = tasks.toMutableList().apply {
            find { it.id == taskId }?.also { remove(it) }
        })
    }
}

fun Observable<TasksViewEvent>.toIntent(): Observable<Intent<TasksModelState>> {
    return map { event ->
        when (event) {
            TasksViewEvent.ClearCompletedClick ->  intent {
                copy( tasks = tasks.filter { !it.completed }.toList() )
            }
            TasksViewEvent.FilterTypeClick -> intent {
                copy( filter = when(filter) {
                    FilterType.ANY -> FilterType.ACTIVE
                    FilterType.ACTIVE -> FilterType.COMPLETE
                    FilterType.COMPLETE -> FilterType.ANY
                })
            }
            TasksViewEvent.RefreshTasksSwipe,
            TasksViewEvent.RefreshTasksClick -> intent {
                this // TODO: Implement network fetches
            }
            is TasksViewEvent.CompleteTaskClick -> intent<TasksModelState> {
                tasks.toMutableList().let {
                    it[tasks.indexOf(event.task)] = event.task.copy(completed = event.checked)
                    copy(tasks = it)
                }
            }
        }
    }
}