package com.kanawish.sample.mvi.view.tasks

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.FilterType

/**
 * View Contracts in an uni-directional data flow setup describe signals emitted by View.
 */
sealed class TasksViewEvent {

    object RefreshTasksPulled : TasksViewEvent() // Pull to refresh / refresh button

    data class TaskClick(val task: Task) : TasksViewEvent() // Should navigate to task detail.

    data class TaskCheckBoxClick(val task:Task, val checked: Boolean) : TasksViewEvent() // Should change 'done' state of a task.

    data class FilterTypeSelected(val type: FilterType) : TasksViewEvent() // Should change current task list filter

    object ClearCompletedTasksClick : TasksViewEvent() // Should archive/soft-delete all 'done' tasks.
}
