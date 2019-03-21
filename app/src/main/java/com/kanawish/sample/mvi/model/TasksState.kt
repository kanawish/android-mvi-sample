package com.kanawish.sample.mvi.model

import java.util.*

fun main() {
    val task = Task(title = "Milk", completed = false)
    // Want to check off milk from the list?
    val updatedTask = task.copy(completed = true)
    // That's all there is to it.

    println("final result: $updatedTask")
}

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
 * Used with the filter selector in the tasks list.
 */
enum class FilterType {
    ANY,
    ACTIVE,
    COMPLETE ;

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

data class TasksState(
    val tasks: List<Task>,
    val filter: FilterType,
    val syncState: SyncState
) {
    fun filteredTasks(): List<Task> {
        return tasks.filter { task -> filter.filter(task) }
    }
}

/*
    State Diagram for SyncState

    @startuml
    [*] --> IDLE
    IDLE --> PROCESS : refresh

    PROCESS --> IDLE : success
    PROCESS --> ERROR : failed

    ERROR --> IDLE : reset
    ERROR: throwable
    @enduml

    Class Diagram for Tasks

    @startuml
    hide empty members
    class Task {
        id: String
        lastUpdate: Long
        title: String
        description: String
        completed: Boolean
    }
    class TasksModelState <<(D,orchid) data class>>{
        tasks:List<Task>,
        filter: FilterType,
        syncState: SyncState
    }
    enum SyncState <<sealed class>> {
        Idle(),
        Process(type),
        Error(throwable)
    }
    enum FilterType {
        ANY
        ACTIVE
        COMPLETE
    }

    TasksModelState*--Task:tasks
    TasksModelState*--FilterType:filter
    TasksModelState*--SyncState:syncState
    @enduml
 */