package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.TaskEditorState

class AddEditTaskIntentFactory {
    companion object {
        /**
         * Creates a intent for the TaskEditorState state machine.
         *
         * Before applying intent, checks that the state machine is in the expected sub-state `<S>`.
         *
         * @param block the intent to move from state `<S>` to a new `TaskEditorState`.
         * @throws IllegalStateException if `<S>` is not the expected type.
         */
        inline fun <reified S : TaskEditorState> editorIntent(
            crossinline block: S.() -> TaskEditorState
        ): Intent<TaskEditorState> {
            return intent {
                (this as? S)?.block()
                    ?: throw IllegalStateException("editorIntent encountered an inconsistent State.")
            }
        }
    }
}