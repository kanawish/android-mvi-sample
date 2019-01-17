package com.kanawish.sample.mvi.model

import android.annotation.SuppressLint
import javax.inject.Inject
import javax.inject.Singleton


@SuppressLint("CheckResult")
@Singleton
class TaskEditorStore @Inject constructor() :
    ModelStore<TaskEditorState>(TaskEditorState.Editing(Task()))

/**
 * State transitions only concern state. They're not responsible for asynchronous jobs or
 * communication with external components.
 */
sealed class TaskEditorState {

    object Closed : TaskEditorState() {
        fun createTask() = Creating
        fun openTask(task: Task) = Editing(task)
    }

    object Creating : TaskEditorState() {
        fun created(task: Task) = Editing(task)
    }

    data class Editing(val task: Task) : TaskEditorState() {
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
    Full State Machine

    @startuml
    [*] --> CLOSED
    CLOSED : error:String?
    CLOSED --> EDITING : openTask
    CLOSED --> CREATING : createTask

    CREATING -down-> EDITING : created
    CREATING --> CLOSED : error

    EDITING : task:Task
    EDITING : error:String?
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
    CLOSED --> EDITING : openTask
    CLOSED --> CREATING : createTask

    CREATING -down-> EDITING : created

    EDITING : task:Task
    EDITING --> EDITING : edit
    EDITING -down-> SAVING : save
    EDITING -down-> DELETING : delete
    EDITING --> CLOSED : cancel

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
    CLOSED --> EDITING : openTask
    CLOSED --> EDITING : createTask

    EDITING : task:Task
    EDITING --> EDITING : edit
    EDITING -down-> CLOSED : save
    EDITING -down-> CLOSED : delete
    EDITING -up-> CLOSED : cancel
    @enduml
 */