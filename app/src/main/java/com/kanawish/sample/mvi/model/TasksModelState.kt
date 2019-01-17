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
    IDLE --> PROCESS : refresh tasks
    IDLE --> PROCESS : create task
    IDLE --> PROCESS : update task

    PROCESS --> [*] : success
    PROCESS --> ERROR : failed
    ERROR-->[*]
    @enduml
 */

