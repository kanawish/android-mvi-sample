package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.TaskEditorState

class AddEditTaskIntentFactory {
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
                    ?: throw IllegalStateException("editorIntent encountered an inconsistent State.")
            }
        }
    }
}