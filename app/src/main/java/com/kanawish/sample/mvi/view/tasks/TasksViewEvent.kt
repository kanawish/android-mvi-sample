package com.kanawish.sample.mvi.view.tasks

import com.kanawish.sample.mvi.model.Task
import io.reactivex.Observable
import io.reactivex.functions.Function3

/**
 * Created on 2017-06-14.
 *
 * View Contracts in an uni-directional data flow setup describe signals emitted by View.
 *
 */
sealed class TasksViewEvent {

    // NOTE: Thoughts on UI & UI events:
    // - Our views are aware of the model, in that they can subscribe to certain attributes.
    // - Views should have not logic regarding inner workings of the model
    // - The UI events emitted should be UI centric.
    // - I think some amount of abstraction is okay. (Doesn't need to be 1-to-1 with android listeners)

    data class TaskClick(val task: Task) : TasksViewEvent() // Should navigate to task detail.

    data class TaskCheckBoxClick(val task:Task, val checked: Boolean) : TasksViewEvent() // Should toggle 'done' state of a task.

    data class FilterTypeSelected(val type: TasksFilterType) : TasksViewEvent() // Should filter list of tasks

    object ClearCompletedTasksClick : TasksViewEvent() // Should archive 'done' tasks.

    object RefreshTasksClick : TasksViewEvent() // TODO: Remove this. Makes little sense in a reactive app.
}
