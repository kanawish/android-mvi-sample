package com.kanawish.sample.mvi.view.addedittask

sealed class AddEditTaskViewEvent {
    data class TitleChange(val title: String): AddEditTaskViewEvent()
    data class DescriptionChange(val description: String) : AddEditTaskViewEvent()
    object SaveTaskClick : AddEditTaskViewEvent()
    object DeleteTaskClick : AddEditTaskViewEvent()
    object CancelTaskClick : AddEditTaskViewEvent()
}