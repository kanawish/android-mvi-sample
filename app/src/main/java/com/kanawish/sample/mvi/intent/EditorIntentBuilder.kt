package com.kanawish.sample.mvi.intent

import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.model.TaskEditorState.Closed
import com.kanawish.sample.mvi.model.TaskEditorState.Editing
import com.kanawish.sample.mvi.model.TasksModelStore
import com.kanawish.sample.mvi.view.edittask.EditTaskViewEvent
import com.kanawish.sample.mvi.view.edittask.EditTaskViewEvent.DeleteTaskClick
import com.kanawish.sample.mvi.view.edittask.EditTaskViewEvent.DescriptionChange
import com.kanawish.sample.mvi.view.edittask.EditTaskViewEvent.EditTaskClick
import com.kanawish.sample.mvi.view.edittask.EditTaskViewEvent.NewTaskClick
import com.kanawish.sample.mvi.view.edittask.EditTaskViewEvent.SaveTaskClick
import com.kanawish.sample.mvi.view.edittask.EditTaskViewEvent.TitleChange
import io.reactivex.Observable
import javax.inject.Inject

/**
 * CONCEPT: When an intent ends up depending on other stores or services,
 * use this "IntentBuilder" pattern.
 *
 * TODO: Consider move to a pure 'Consume<AddEditTaskViewEvent>' mode vs 'toIntents()'
 */
class EditorIntentBuilder @Inject constructor(
    private val tasksModelStore: TasksModelStore
) {
    /**
     * More complex example of an intent using retrofit and async calls.
     */
    private fun newTaskIntent() = editorIntent<Closed> {
        // FIXME: We're faking async here. Use retrofit to demo async.
        createTask().run {
            val newTask = Task()
            tasksModelStore.process(addNewTask(newTask))
            created(task = newTask)
        }
    }

    private fun saveEditedTaskIntent() = editorIntent<Editing> {
        // FIXME: We're faking async here. Use retrofit to demo async.
        save().run {
            tasksModelStore.process(saveTaskIntent(task))
            saved()
        }
    }

    private fun deleteEditedTaskIntent() = editorIntent<Editing> {
        // FIXME: We're faking async here. Use retrofit to demo async.
        delete().run {
            tasksModelStore.process(deleteTaskIntent(taskId))
            deleted()
        }
    }

    // Shorthand for our DSL, or use unchecked cast...
    private inline fun <reified S : TaskEditorState> editorIntent(crossinline init: S.() -> TaskEditorState) =
        checkedIntent(init)

    fun Observable<EditTaskViewEvent>.toIntent(): Observable<Intent<TaskEditorState>> {
        return map { event ->
            when (event) {
                NewTaskClick -> newTaskIntent()
                is EditTaskClick -> editorIntent<Closed> {
                    openTask(event.task)
                }
                is TitleChange -> editorIntent<Editing> {
                    edit { copy(title = event.title) }
                }
                is DescriptionChange -> editorIntent<Editing> {
                    edit { copy(description = event.description) }
                }
                is SaveTaskClick -> saveEditedTaskIntent()
                is DeleteTaskClick -> deleteEditedTaskIntent()
            }
        }
    }

}

fun Observable<EditTaskViewEvent>.toIntent(builder:EditorIntentBuilder) = builder.run { toIntent() }