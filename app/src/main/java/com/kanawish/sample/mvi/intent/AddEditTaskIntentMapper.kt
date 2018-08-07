package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.EditState
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.model.TaskEditorState.Companion.taskBlock
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent
import io.reactivex.Observable

fun Observable<AddEditTaskViewEvent>.toIntent(): Observable<Intent<TaskEditorState>> {
    return map { event ->
        when (event) {
            is AddEditTaskViewEvent.TitleChange -> taskBlock {
                copy(title = event.title)
            }
            is AddEditTaskViewEvent.DescriptionChange -> taskBlock {
                copy(description = event.description)
            }
            AddEditTaskViewEvent.SaveTaskClick -> intervalBlocksIntent(1,
                    { copy(editState = EditState.SAVING) },
                    { copy(editState = EditState.CLOSED) }
            )
            AddEditTaskViewEvent.DeleteTaskClick -> intervalBlocksIntent(1,
                    { copy(editState = EditState.DELETING) },
                    { copy(editState = EditState.CLOSED) }
            )
        }
    }
}
