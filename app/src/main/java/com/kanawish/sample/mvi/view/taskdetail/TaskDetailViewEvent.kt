package com.kanawish.sample.mvi.view.taskdetail

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.view.tasks.TasksViewEvent

/**
 * Created on 2017-07-10.
 */
sealed class TaskDetailViewEvent {
    data class TaskCheckBoxClick(val taskId: String) : TaskDetailViewEvent()

}