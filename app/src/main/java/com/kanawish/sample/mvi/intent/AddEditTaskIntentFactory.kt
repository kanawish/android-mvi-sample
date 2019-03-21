package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TaskEditorModelStore
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.model.TaskEditorState.Closed
import com.kanawish.sample.mvi.model.TaskEditorState.Editing
import com.kanawish.sample.mvi.model.TasksModelStore
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.CancelTaskClick
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.DeleteTaskClick
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.DescriptionChange
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.SaveTaskClick
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.TitleChange
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AddEditTaskIntentFactory is responsible for turning AddEditTaskViewEvents into
 * Intent<TaskEditorState>, and coordinates with any other dependencies, such as
 * ModelStores, Repositories or Services.
 */
@Singleton class AddEditTaskIntentFactory @Inject constructor(
    private val taskEditorModelStore: TaskEditorModelStore,
    private val tasksModelStore: TasksModelStore
) {

    fun process(viewEvent: AddEditTaskViewEvent) {
        taskEditorModelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: AddEditTaskViewEvent): Intent<TaskEditorState> {
        return when (viewEvent) {
            is TitleChange -> buildEditTitleIntent(viewEvent)
            is DescriptionChange -> buildEditDescriptionIntent(viewEvent)
            SaveTaskClick -> buildSaveIntent()
            DeleteTaskClick -> buildDeleteIntent()
            CancelTaskClick -> buildCancelIntent()
        }
    }

    /**
     * An example of delegating work to an external dependency.
     */
    private fun buildSaveIntent() = editorIntent<Editing> {
        // This triggers a state change in another ModelStore.
        save().run {
            // NOTE: When we do this with a real backend + retrofit, it will become asynchronous.
            val intent = TasksIntentFactory.buildAddOrUpdateTaskIntent(task)
            tasksModelStore.process(intent)
            saved()
        }
    }

    private fun buildDeleteIntent() = editorIntent<Editing> {
        delete().run {
            // `TasksStore` deletes this task from its internal list.
            val intent = TasksIntentFactory.buildDeleteTaskIntent(taskId)
            tasksModelStore.process(intent)
            deleted()
        }
    }

    companion object {
        /**
         * Creates an intent for the TaskEditor state machine.
         *
         * Utility function to cut down on boilerplate.
         */
        inline fun <reified S : TaskEditorState> editorIntent (
            crossinline block: S.() -> TaskEditorState
        ) : Intent<TaskEditorState> {
            return intent {
                (this as? S)?.block()
                    ?: throw IllegalStateException("editorIntent encountered an inconsistent State. [Looking for ${S::class.java} but was ${this.javaClass}]")
            }
        }

        fun buildAddTaskIntent(task: Task) = editorIntent<Closed> { addTask(task) }

        fun buildEditTaskIntent(task: Task) = editorIntent<Closed> { editTask(task) }

        private fun buildEditTitleIntent(viewEvent: TitleChange) = editorIntent<Editing> {
            edit { copy( title = viewEvent.title ) }
        }

        private fun buildEditDescriptionIntent(viewEvent: DescriptionChange) = editorIntent<Editing> {
            edit { copy( description= viewEvent.description) }
        }

        private fun buildCancelIntent() = editorIntent<Editing> { cancel() }
    }

}