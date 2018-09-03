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
    COMPLETE()    // Filters only the completed tasks.
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
data class TasksModelState(
    val tasks: List<Task>,
    val filter: FilterType,
    val syncState: SyncState
)

/*
    State Diagram for SyncState

    @startuml
    [*] --> IDLE
    IDLE --> PROCESS : refresh
    IDLE --> PROCESS : check task

    PROCESS -left-> IDLE : success
    PROCESS --> ERROR : failed
    ERROR-->IDLE
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

