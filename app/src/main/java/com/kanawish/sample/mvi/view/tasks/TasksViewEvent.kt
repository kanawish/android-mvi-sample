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

    data class TaskClick(val task: Task) : TasksViewEvent() // Doesn't concern the model as it is now.

    data class TaskCheckBoxClick(val task: Task) : TasksViewEvent()

    data class FilterTypeSelected(val type: TasksFilterType) : TasksViewEvent()

    object ClearCompletedTasksClick : TasksViewEvent()

    object RefreshTasksClick : TasksViewEvent()
}