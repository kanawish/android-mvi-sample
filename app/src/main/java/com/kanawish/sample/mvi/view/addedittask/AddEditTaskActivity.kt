package com.kanawish.sample.mvi.view.addedittask

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.EditorIntentBuilder
import com.kanawish.sample.mvi.intent.toIntent
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.model.TaskEditorStore
import com.kanawish.sample.mvi.util.replaceFragmentInActivity
import com.kanawish.sample.mvi.util.setupActionBar
import com.kanawish.sample.mvi.view.ViewContract
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.DeleteTaskClick
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.SaveTaskClick
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.addtask_act.busy
import kotlinx.android.synthetic.main.addtask_act.fab_edit_task_done
import kotlinx.android.synthetic.main.addtask_act.toolbar
import timber.log.Timber
import javax.inject.Inject

/**
 * Activity houses the Toolbar, a FAB and the fragment for adding/editing tasks.
 */
class AddEditTaskActivity : AppCompatActivity(), ViewContract<AddEditTaskViewEvent, TaskEditorState> {

    @Inject lateinit var editorStore: TaskEditorStore
    @Inject lateinit var intentBuilder: EditorIntentBuilder

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)

        fab_edit_task_done?.apply {
            setImageResource(R.drawable.ic_done)
        }

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
                ?: AddEditTaskFragment().also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // NOTE: The subscription will execute inline, since we're subscribing from the main thread.
        disposables += editorStore.modelState().firstElement().subscribe {
            menu.findItem(R.id.menu_delete).isEnabled = it is TaskEditorState.Editing
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        disposables += editorStore.modelState().subscribeView()
        disposables += events().toIntent(intentBuilder).subscribe(editorStore::process)
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun Observable<TaskEditorState>.subscribeView(): Disposable {
        return CompositeDisposable().also { consumers ->
            consumers += subscribe { Timber.i("$it") }
            consumers += subscribe {
                when (it) {
                    is TaskEditorState.Editing -> {
                        busy.visibility = GONE
                    }
                    is TaskEditorState.Saving, is TaskEditorState.Deleting -> {
                        invalidateOptionsMenu()
                        fab_edit_task_done.isEnabled = false
                        busy.visibility = VISIBLE
                    }
                    TaskEditorState.Closed -> {
                         onBackPressed()
                    }
                }
            }
        }
    }

    override fun events(): Observable<AddEditTaskViewEvent> {
        return Observable.merge(
                toolbar.itemClicks()
                        .filter { it.itemId == R.id.menu_delete }
                        .map { DeleteTaskClick },
                fab_edit_task_done.clicks().map { SaveTaskClick }
        )
    }

}