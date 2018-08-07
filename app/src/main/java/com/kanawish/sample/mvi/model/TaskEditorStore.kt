package com.kanawish.sample.mvi.model

import com.kanawish.sample.mvi.intent.Intent
import com.kanawish.sample.mvi.intent.singleBlockIntent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskEditorStore @Inject constructor() :
        ModelStore<TaskEditorState>(
                TaskEditorState(
                        Task("bogusId", System.currentTimeMillis()),
                        EditState.EDITING)
        )

data class TaskEditorState(val task: Task?, val editState: EditState) {
    companion object {
        fun taskBlock(block: Task.() -> Task): Intent<TaskEditorState> {
            return singleBlockIntent { copy(task = task?.block()) }
        }
    }
}

enum class EditState {
    CLOSED,
    EDITING,
    SAVING,
    DELETING
}

/*
    Edit State Machine

    @startuml
    [*] --> CLOSED
    CLOSED --> EDITING : edit

    EDITING --> SAVING : save
    EDITING --> CLOSED : cancel

    SAVING --> EDITING : error
    SAVING --> CLOSED : success
    @enduml
 */
