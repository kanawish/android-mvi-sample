package com.kanawish.sample.mvi.view.tasks

import com.kanawish.sample.mvi.model.FilterType
import com.kanawish.sample.mvi.model.Task

/**
 * View Contracts in an uni-directional data flow
 * setup describe signals emitted by View.
 */
sealed class TasksViewEvent {

    // Pull to refresh / refresh button
    object RefreshTasksPulled : TasksViewEvent()

    // Should navigate to task detail.
    data class TaskClick(val task: Task) : TasksViewEvent()

    // Should change 'done' state of a task.
    data class TaskCheckBoxClick(val task: Task, val checked: Boolean) :
            TasksViewEvent()

    // Should change current task list filter
    data class FilterTypeSelected(val type: FilterType) : TasksViewEvent()

    // Should archive/soft-delete all 'done' tasks.
    object ClearCompletedTasksClick : TasksViewEvent()
}
