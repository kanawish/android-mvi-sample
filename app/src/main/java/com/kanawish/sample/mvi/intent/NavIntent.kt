package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Task

/**
 * Created on 2017-07-10.
 */
sealed class NavIntent {

    class TaskDetails(val taskId: String) : NavIntent() {
        constructor(task: Task) : this(task.id)
    }

    class EditTask(val taskId: String) : NavIntent() {
        constructor(task: Task) : this(task.id)
    }

}