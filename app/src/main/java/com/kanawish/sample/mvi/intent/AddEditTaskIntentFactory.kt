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
 * AddEditTaskIntentFactory is responsible for turning AddEditTaskViewEvent into
 * Intent<TaskEditorState>, and coordinates with any other dependencies such as
 * ModelStores, Repositories or Services.

 * NOTE: AddEditTaskIntentFactory will take the state-machine validity approach.
 *
 * @see TasksIntentFactory  for lightweight "assert()" approach.
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
     *
     * - Within the "SAVING" state scope, we use `task` to trigger our external save call.
     */
    private fun buildSaveIntent() = editorIntent<Editing> {
        // NOTE: We'll jump from EDITING -> SAVING -> SAVE in one step, since we're synchronous for now.
        // We first call `save()`, transitioning "EDITING" -> "SAVING".
        save().run {
            // NOTE: When we'll do this with a real backend + Retrofit, this will become asynchronous.
            // `TasksStore` add/update this task.
            val intent = TasksIntentFactory.buildAddOrUpdateTaskIntent(task)
            tasksModelStore.process(intent)

            // We call `saved()`, transitioning "SAVING" -> "CLOSED"
            saved()
        }
    }

    /**
     * NOTE: Like `buildSaveIntent()` above, we're faking async.
     */
    private fun buildDeleteIntent() = editorIntent<Editing> {
        // "EDITING" -> "DELETING"
        delete().run {
            // `TasksStore` deletes this tasks from its internal list.
            val intent = TasksIntentFactory.buildDeleteTaskIntent(taskId)
            tasksModelStore.process(intent)

            // "DELETING" -> "CLOSED"
            deleted()
        }
    }

    /**
     * Using the companion object allows us to clearly identify the intent builders
     * that have no external dependencies.
     */
    companion object {

        /**
         * Utility DSL function.
         *
         * This function creating a single-reducer intent for our TaskEditorState state machine.
         *
         * It checks that the state machine is in the correct state for this intent, and
         * throws an IllegalStateException if it is not.
         */
        private inline fun <reified S : TaskEditorState> editorIntent(
            crossinline block: S.() -> TaskEditorState
        ): Intent<TaskEditorState> {
            return intent {
                (this as? S)?.block()
                    ?: throw IllegalStateException("editorIntent encountered an inconsistent State.")
            }
        }

        private fun buildEditTitleIntent(viewEvent: TitleChange) = editorIntent<Editing> {
            edit { copy(title = viewEvent.title) }
        }

        private fun buildEditDescriptionIntent(viewEvent: DescriptionChange) = editorIntent<Editing> {
            edit { copy(description = viewEvent.description) }
        }

        private fun buildCancelIntent() = editorIntent<Editing> {
            cancel()
        }

        fun buildAddTaskIntent(task: Task) = editorIntent<Closed> {
            addTask(task)
        }

        fun buildEditTaskIntent(task: Task) = editorIntent<Closed> {
            editTask(task)
        }

    }
}

