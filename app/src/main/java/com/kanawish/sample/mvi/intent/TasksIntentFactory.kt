package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.TaskEditorModelStore
import com.kanawish.sample.mvi.model.TasksModelStore
import com.kanawish.sample.mvi.model.TasksState
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksIntentFactory @Inject constructor(
    private val tasksModelStore: TasksModelStore,
    private val taskEditorModelStore: TaskEditorModelStore
){
    fun process(event:TasksViewEvent) {
        tasksModelStore.process(toIntent(event))
    }

    private fun toIntent(viewEvent: TasksViewEvent): Intent<TasksState> {
        return when(viewEvent) {
            ClearCompletedClick -> TODO()
            FilterTypeClick -> TODO()
            NewTaskClick -> TODO()
            RefreshTasksClick -> TODO()
            RefreshTasksSwipe -> TODO()
            is CompleteTaskClick -> TODO()
            is EditTaskClick -> TODO()
        }
    }
}