package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TaskEditorModelStore
import com.kanawish.sample.mvi.model.TasksModelStore
import com.kanawish.sample.mvi.model.TasksState
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.ClearCompletedClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.CompleteTaskClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.EditTaskClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.FilterTypeClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.NewTaskClick
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent.RefreshTasksSwipe
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
            ClearCompletedClick -> TODO()
            FilterTypeClick -> TODO()
            RefreshTasksSwipe, TasksViewEvent.RefreshTasksClick -> TODO()
            is CompleteTaskClick -> buildCompleteTaskClick(viewEvent)
            NewTaskClick -> buildNewTaskIntent()
            is EditTaskClick -> buildEditTaskIntent(viewEvent)
        }
    }

    private fun buildEditTaskIntent(viewEvent: EditTaskClick): Intent<TasksState> {
        // NOTE: We use `sideEffect{}` since we're entirely delegating the work.
        return sideEffect {
            // We can assert things about the TasksStore state.
            assert(tasks.contains(viewEvent.task))

            // Editing a task then only involves opening it.
            val intent = AddEditTaskIntentFactory.buildEditTaskIntent(viewEvent.task)
            taskEditorModelStore.process(intent)
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

    private fun buildCompleteTaskClick(viewEvent: CompleteTaskClick): Intent<TasksState> {
        return intent {
            // We need to operate on the tasks list.
            val mutableList = tasks.toMutableList()
            // Replaces old task in the list with a new updated copy.
            mutableList[tasks.indexOf(viewEvent.task)] = viewEvent.task.copy(completed = viewEvent.checked)
            // Take the modified list, and create a new copy of tasksState with it.
            copy(tasks = mutableList)
        }
    }

    companion object {
        /** Allows an external Model to save a task. */
        fun buildAddOrUpdateTaskIntent(task: Task): Intent<TasksState> = intent {
            tasks.toMutableList().let { newList ->
                newList.find { task.id == it.id }?.let {
                    newList[newList.indexOf(it)] = task
                } ?: newList.add(task)

                copy(tasks = newList)
            }
        }

        /** Allows an external model to delete a task. */
        fun buildDeleteTaskIntent(taskId: String): Intent<TasksState> = intent {
            copy(tasks = tasks.toMutableList().apply {
                find { it.id == taskId }?.also { remove(it) }
            })
        }
    }

}
