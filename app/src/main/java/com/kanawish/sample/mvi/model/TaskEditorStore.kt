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

/**
 * State transitions only concern state. They're not responsible for asynchronous jobs or
 * communication with external components.
 */
sealed class TaskEditorState {

    object Closed : TaskEditorState() {
        fun newTask() = Creating
        fun editTask(task: Task) = Editing(task)
    }

    object Creating : TaskEditorState() {
        fun editTask(task: Task) = Editing(task)
    }

    data class Editing(val task: Task) : TaskEditorState() {
        fun edit(block: Task.() -> Task) = copy(task = task.block())
        fun save() = Saving(task)
        fun delete() = Deleting(task.id)
        fun cancel() = Closed
    }

    data class Saving(val task: Task) : TaskEditorState() {
        fun done() = Closed
    }

    data class Deleting(val taskId: String) : TaskEditorState() {
        fun done() = Closed
    }
}

/*
    Edit State Machine

    @startuml
    [*] --> CLOSED
    CLOSED --> EDITING : editTask
    CLOSED --> CREATING : createTask
    CREATING --> EDITING : editTask

    EDITING : task
    EDITING --> EDITING : edit
    EDITING --> SAVING : save
    EDITING --> DELETING : delete
    EDITING -up-> CLOSED : cancel

    DELETING -> CLOSED : done
    DELETING : taskId

    SAVING --> CLOSED : done
    SAVING : task

    @enduml
 */
