package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.model.TaskEditorState.Closed
import com.kanawish.sample.mvi.model.TaskEditorState.Editing
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.DeleteTaskClick
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.DescriptionChange
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.SaveTaskClick
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.TitleChange
import io.reactivex.Observable

fun Observable<AddEditTaskViewEvent>.toIntent(): Observable<Intent<TaskEditorState>> {
    return map { event ->
        when (event) {
            is TitleChange ->
                editorIntent<Editing> { edit { copy(title = event.title) } }
            is DescriptionChange ->
                editorIntent<Editing> { edit { copy(description = event.description) } }
            is SaveTaskClick ->
                editorIntent<Editing> { save() }
            is DeleteTaskClick ->
                editorIntent<Editing> { delete() }
        }
    }
}

/**
 * For now, we'll take a hard line against inconsistent view events, and crash the app.
 */
inline fun <reified S : TaskEditorState> editorIntent(crossinline block: S.() -> TaskEditorState?) =
        checkedIntent(block) { throw IllegalStateException("Reducer encountered an inconsistent State.") }

