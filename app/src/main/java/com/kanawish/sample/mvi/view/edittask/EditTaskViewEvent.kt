package com.kanawish.sample.mvi.view.edittask

import com.kanawish.sample.mvi.model.Task

sealed class EditTaskViewEvent {
    object NewTaskClick : EditTaskViewEvent()
    data class EditTaskClick(val task: Task) : EditTaskViewEvent()
    data class TitleChange(val title: String) : EditTaskViewEvent()
    data class DescriptionChange(val description: String) : EditTaskViewEvent()
    object SaveTaskClick : EditTaskViewEvent()
    object DeleteTaskClick : EditTaskViewEvent()
}