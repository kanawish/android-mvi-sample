package com.kanawish.sample.mvi.view.addedittask

import com.kanawish.sample.mvi.model.Task

sealed class AddEditTaskViewEvent {
    object NewTaskClick : AddEditTaskViewEvent()
    data class EditTaskClick(val task: Task) : AddEditTaskViewEvent()
    data class TitleChange(val title: String) : AddEditTaskViewEvent()
    data class DescriptionChange(val description: String) : AddEditTaskViewEvent()
    object SaveTaskClick : AddEditTaskViewEvent()
    object DeleteTaskClick : AddEditTaskViewEvent()
}