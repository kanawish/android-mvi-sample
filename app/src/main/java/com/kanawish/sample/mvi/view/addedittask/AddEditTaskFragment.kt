package com.kanawish.sample.mvi.view.addedittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding2.widget.textChanges
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.AddEditTaskIntentFactory
import com.kanawish.sample.mvi.model.TaskEditorModelStore
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.view.EventObservable
import com.kanawish.sample.mvi.view.StateSubscriber
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.DescriptionChange
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.TitleChange
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.addtask_frag.add_task_description
import kotlinx.android.synthetic.main.addtask_frag.add_task_title
import javax.inject.Inject

/**
 * Fragment for adding/editing tasks.
 */
class AddEditTaskFragment : Fragment(),
    StateSubscriber<TaskEditorState>,
    EventObservable<AddEditTaskViewEvent> {

    @Inject lateinit var editorModelStore: TaskEditorModelStore
    @Inject lateinit var intentFactory: AddEditTaskIntentFactory

    private val disposables = CompositeDisposable()

    override fun events(): Observable<AddEditTaskViewEvent> {
        return Observable.merge(
                add_task_title.textChanges().map { TitleChange(it.toString()) },
                add_task_description.textChanges().map { DescriptionChange(it.toString()) }
        )
    }

    override fun Observable<TaskEditorState>.subscribeToState(): Disposable {
        return ofType<TaskEditorState.Editing>().firstElement().subscribe { editing ->
            add_task_title.setText(editing.task.title)
            add_task_description.setText(editing.task.description)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.addtask_frag, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addtask_fragment_menu, menu)
    }

    override fun onResume() {
        super.onResume()
        disposables += editorModelStore.modelState().subscribeToState()
        disposables += events().subscribe(intentFactory::process)
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

}