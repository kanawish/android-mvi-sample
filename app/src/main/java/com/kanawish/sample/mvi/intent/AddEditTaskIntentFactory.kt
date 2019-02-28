package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.TaskEditorModelStore
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.model.TaskEditorState.Editing
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
    private val taskEditorModelStore: TaskEditorModelStore
) {

    fun process(viewEvent: AddEditTaskViewEvent) {
        taskEditorModelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: AddEditTaskViewEvent): Intent<TaskEditorState> {
        return when (viewEvent) {
            is TitleChange -> buildEditTitleIntent(viewEvent)
            is DescriptionChange -> buildEditDescriptionIntent(viewEvent)
            SaveTaskClick -> TODO()
            DeleteTaskClick -> TODO()
            CancelTaskClick -> TODO()
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

        private fun buildEditTitleIntent(viewEvent: TitleChange) = editorIntent<Editing> {
            edit { copy( title = viewEvent.title ) }
        }

        private fun buildEditDescriptionIntent(viewEvent: DescriptionChange) = editorIntent<Editing> {
            edit { copy( description= viewEvent.description) }
        }

    }

}