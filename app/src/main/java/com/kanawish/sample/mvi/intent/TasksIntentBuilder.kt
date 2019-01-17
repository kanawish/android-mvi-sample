package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TasksModelState

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