package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TaskEditorModelStore
import com.kanawish.sample.mvi.model.TasksModelStore
import com.kanawish.sample.mvi.model.TasksState
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksIntentFactory @Inject constructor(
    private val tasksModelStore: TasksModelStore,
    private val taskEditorModelStore: TaskEditorModelStore
) {

    fun process(event: TasksViewEvent) {
        tasksModelStore.process(toIntent(event))
    }

    private fun toIntent(viewEvent:TasksViewEvent): Intent<TasksState> {
        return when (viewEvent) {
            TasksViewEvent.ClearCompletedClick -> TODO()
            TasksViewEvent.FilterTypeClick -> TODO()
            TasksViewEvent.RefreshTasksSwipe, TasksViewEvent.RefreshTasksClick -> TODO()
            is TasksViewEvent.CompleteTaskClick -> TODO()
            TasksViewEvent.NewTaskClick -> buildNewTaskIntent()
            is TasksViewEvent.EditTaskClick -> TODO()
        }
    }

    private fun buildNewTaskIntent(): Intent<TasksState> {
        // NOTE: We use `sideEffect{}` since we're entirely delegating the work.
        return sideEffect {
            // Opening a new task for editing.
            val intent = AddEditTaskIntentFactory.buildAddTaskIntent(Task())
            taskEditorModelStore.process(intent)
        }
    }

}
