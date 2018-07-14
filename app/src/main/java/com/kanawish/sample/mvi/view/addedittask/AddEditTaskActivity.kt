package com.kanawish.sample.mvi.view.addedittask

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.AddEditTaskIntentFactory
import com.kanawish.sample.mvi.model.TaskEditorModelStore
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.util.replaceFragmentInActivity
import com.kanawish.sample.mvi.util.setupActionBar
import com.kanawish.sample.mvi.view.EventObservable
import com.kanawish.sample.mvi.view.StateSubscriber
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent.CancelTaskClick
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
class AddEditTaskActivity : AppCompatActivity(),
    StateSubscriber<TaskEditorState>,
    EventObservable<AddEditTaskViewEvent> {

    @Inject lateinit var editorModelStore: TaskEditorModelStore
    @Inject lateinit var intentFactory: AddEditTaskIntentFactory

    /**
     * NOTE: Relays are useful when RxBinding doesn't expose a binding.
     * @see onSupportNavigateUp
     */
    private val navigateUpRelay: PublishRelay<CancelTaskClick> = PublishRelay.create<CancelTaskClick>()

    private val disposables = CompositeDisposable()

    override fun Observable<TaskEditorState>.subscribeToState(): Disposable {
        return CompositeDisposable().also { disposables ->
            // Logging
            disposables += subscribe { Timber.i("$it") }
            // Reactive UX
            disposables += subscribe {
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

    /**
     * On Android, not everything is a straightforward click listener.
     *
     */
    override fun events(): Observable<AddEditTaskViewEvent> {
        return Observable.merge(
                toolbar.itemClicks()
                    .filter { it.itemId == R.id.menu_delete }
                    .map { DeleteTaskClick },
                fab_edit_task_done.clicks().map { SaveTaskClick },
                navigateUpRelay
        )
    }

    /**
     * NOTE: When dealing with menus and such, we need "lifecycle flexibility".
     *
     * Here, `invalidateOptionsMenu()` is triggered from our long-lived subscription above
     * and we are a one-time trigger since we use the `firstElement()` operator below.
     *
     * If you breakpoint through the code below, you should see that the subscription
     * resolves synchronously, not asynchronously!
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        disposables += editorModelStore.modelState()
            .firstElement()
            .subscribe { state ->
                menu.findItem(R.id.menu_delete).apply {
                    val itemEnabled = state is TaskEditorState.Editing && !state.adding
                    isVisible = itemEnabled
                    isEnabled = itemEnabled
                }
            }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateUpRelay.accept(CancelTaskClick)
        return true
    }

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