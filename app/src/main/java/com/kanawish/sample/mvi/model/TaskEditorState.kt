package com.kanawish.sample.mvi.model

/**
 * State transitions only concern state. They're not responsible for asynchronous jobs or
 * communication with external components.
 */
sealed class TaskEditorState {

    object Closed : TaskEditorState() {
        fun addTask(task: Task) = Editing(task, true)
        fun editTask(task: Task) = Editing(task)
    }

    data class Editing(val task: Task, val adding: Boolean = false) : TaskEditorState() {
        fun edit(block: Task.() -> Task) = copy(task = task.block())
        fun save() = Saving(task)
        fun delete() = Deleting(task.id)
        fun cancel() = Closed
    }

    data class Saving(val task: Task) : TaskEditorState() {
        fun saved() = Closed
    }

    data class Deleting(val taskId: String) : TaskEditorState() {
        fun deleted() = Closed
    }

}

/*
    What would be a full State Machine with errors

    @startuml
    [*] --> CLOSED
    CLOSED --> EDITING : editTask

    EDITING : task
    EDITING : error?
    EDITING --> EDITING : edit
    EDITING -down-> SAVING : save
    EDITING -down-> DELETING : delete
    EDITING -left-> CLOSED : cancel

    DELETING -up-> CLOSED : deleted
    DELETING --> EDITING : error
    DELETING : taskId

    SAVING -up-> CLOSED : saved
    SAVING --> EDITING : error
    SAVING : task
    @enduml
 */

/*
   Simplified State Machine [no errors]

    @startuml
    [*] --> CLOSED
    CLOSED --> EDITING : editTask

    EDITING : task
    EDITING --> EDITING : edit
    EDITING -down-> SAVING : save
    EDITING -down-> DELETING : delete
    EDITING -left-> CLOSED : cancel

    DELETING --> CLOSED : deleted
    DELETING : taskId

    SAVING -up-> CLOSED : saved
    SAVING : task
    @enduml
 */

/*
   Naive State Machine [no async]

    @startuml
    [*] --> CLOSED
    CLOSED --> EDITING : editTask

    EDITING : task
    EDITING --> EDITING : edit
    EDITING -down-> CLOSED : save
    EDITING -down-> CLOSED : delete
    EDITING -up-> CLOSED : cancel
    @enduml
 */