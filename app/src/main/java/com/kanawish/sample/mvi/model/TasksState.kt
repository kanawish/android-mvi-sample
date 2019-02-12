package com.kanawish.sample.mvi.model

import java.util.*

/**
 * Task Model
 */
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val lastUpdate: Long = -1,
    val title: String = "New Task",
    val description: String = "",
    val completed: Boolean = false
)

/**
 * Used with the filter spinner in the tasks list.
 */
enum class FilterType {
    ANY(),        // Do not filter tasks.
    ACTIVE(),     // Filters only the active (not completed yet) tasks.
    COMPLETE();   // Filters only the completed tasks.

    fun filter(task: Task): Boolean {
        return when (this) {
            ANY -> true
            ACTIVE -> !task.completed
            COMPLETE -> task.completed
        }
    }
}

/**
 * Tasks Sync State
 *
 * Represents the app's current synchronization state.
 */
sealed class SyncState {
    object IDLE : SyncState() {
        override fun toString(): String = "IDLE"
    }

    data class PROCESS(val type: Type) : SyncState() {
        enum class Type {
            REFRESH, CREATE, UPDATE
        }
    }

    data class ERROR(val throwable: Throwable) : SyncState()
}

/**
 * ModelState holds all the states we track in the Model.
 */
data class TasksState(
    val tasks: List<Task>,
    val filter: FilterType,
    val syncState: SyncState
) {
    fun filteredTasks(): List<Task> = tasks.filter(filter::filter)
}

/*
    State Diagram for SyncState

    @startuml
    [*] --> IDLE
    IDLE --> PROCESS : refresh
    IDLE --> PROCESS : check task

    PROCESS -left-> IDLE : success
    PROCESS --> ERROR : failed
    PROCESS: type
    ERROR-->IDLE
    ERROR: throwable
    @enduml
    
    Class Diagram for Tasks 
    
   @startuml
   hide empty members
   class TasksModelState << (D,orchid) data class >>{
       tasks: List<Task>
       filter: FilterType
       syncState: SyncState
   }
   class Task {
   id: String
   lastUpdate: Long
   title: String
   description: String
   completed: Boolean
   }
   enum SyncState << (S,#FF7700) sealed class >> {
   Idle()
   Process(type)
   Error(details)
   }
   enum FilterType {
   ANY()
   ACTIVE()
   COMPLETE()
   }

   TasksModelState*--Task:tasks
   TasksModelState*--SyncState:syncState
   TasksModelState*--FilterType:filter
   @enduml
 */

