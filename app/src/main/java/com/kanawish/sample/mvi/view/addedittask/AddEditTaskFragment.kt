package com.kanawish.sample.mvi.view.addedittask

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.textChanges
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.EditorIntentBuilder
import com.kanawish.sample.mvi.intent.toIntent
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.model.TaskEditorStore
import com.kanawish.sample.mvi.view.ViewContract
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.DescriptionChange
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.TitleChange
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.addtask_act.fab_edit_task_done
import kotlinx.android.synthetic.main.addtask_frag.add_task_description
import kotlinx.android.synthetic.main.addtask_frag.add_task_title
import javax.inject.Inject

/**
 * Fragment for adding/editing tasks.
 */
class AddEditTaskFragment : Fragment(), ViewContract<AddEditTaskViewEvent, TaskEditorState> {

    @Inject lateinit var editorStore: TaskEditorStore
    @Inject lateinit var intentBuilder: EditorIntentBuilder

    private val disposables = CompositeDisposable()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fab_edit_task_done?.apply {
            setImageResource(R.drawable.ic_done)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.addtask_frag, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.addtask_fragment_menu, menu)
    }

    override fun onResume() {
        super.onResume()
        disposables += events().toIntent(intentBuilder).subscribe(editorStore::process)
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun events(): Observable<AddEditTaskViewEvent> {
        return Observable.merge(
                add_task_title.textChanges().map { TitleChange(it.toString()) },
                add_task_description.textChanges().map { DescriptionChange(it.toString()) }
        )
    }
}