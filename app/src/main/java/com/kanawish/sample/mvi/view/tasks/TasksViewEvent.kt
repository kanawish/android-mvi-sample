package com.kanawish.sample.mvi.view.tasks

import com.kanawish.sample.mvi.model.Task

sealed class TasksViewEvent {
    object NewTaskClick : TasksViewEvent()
    object FilterTypeClick : TasksViewEvent()
    object ClearCompletedClick : TasksViewEvent()
    object RefreshTasksClick : TasksViewEvent()
    object RefreshTasksSwipe : TasksViewEvent()
    data class CompleteTaskClick(val task: Task, val checked: Boolean) : TasksViewEvent()
    data class EditTaskClick(val task: Task) : TasksViewEvent()
}