package com.kanawish.sample.mvi.view.tasks

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.material.itemSelections
import com.jakewharton.rxbinding3.widget.itemSelections
import com.jakewharton.rxbinding3.view.clicks
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.TasksIntentFactory
import com.kanawish.sample.mvi.model.TaskEditorModelStore
import com.kanawish.sample.mvi.model.TaskEditorState
import com.kanawish.sample.mvi.util.replaceFragmentInActivity
import com.kanawish.sample.mvi.util.setupActionBar
import com.kanawish.sample.mvi.view.EventObservable
import com.kanawish.sample.mvi.view.StateSubscriber
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskActivity
import com.kanawish.sample.mvi.view.statistics.StatisticsActivity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.tasks_act.*
import javax.inject.Inject

/**
 * Tasks Activity houses the Toolbar, the nav UI, the FAB and the fragment holding the tasks list.
 */
class TasksActivity : AppCompatActivity(),
    StateSubscriber<TaskEditorState>,
    EventObservable<TasksViewEvent>
{
    // NOTE: We connect to _editor_ model here.
    @Inject lateinit var editorModelStore: TaskEditorModelStore

    // NOTE: We still only generate "Tasks" ViewEvents.
    @Inject lateinit var tasksIntentFactory: TasksIntentFactory

    private val disposables = CompositeDisposable()

    /**
     * TasksActivity starts the AddEditTaskActivity when it detects the
     * `TaskEditorStore` has transitioned to a `TaskEditorState.Editing` state.
     */
    override fun Observable<TaskEditorState>.subscribeToState(): Disposable {
        return ofType<TaskEditorState.Editing>().subscribe {
            // The Android kind
            val intent = Intent(this@TasksActivity, AddEditTaskActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * TasksActivity owns the Floating Action Button, and is the source
     * for `TasksViewEvent.NewTaskClick` events.
     */
    override fun events(): Observable<TasksViewEvent> {
        return newTaskFloatingActionButton.clicks().map { TasksViewEvent.NewTaskClick }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        // Set up the navigation drawer.
        drawerLayout.apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }

        // Use existing content fragment, or create one from scratch.
        supportFragmentManager.findFragmentById(R.id.contentFrame) as TasksFragment?
            ?: TasksFragment().also {
                replaceFragmentInActivity(it, R.id.contentFrame)
            }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        disposables += subscribeNavHandling()
        disposables += events().subscribe(tasksIntentFactory::process)
        disposables += editorModelStore.modelState().subscribeToState()
    }

    // NOTE: If something doesn't impact your Model/Domain, it's ok to call it "ViewLogic".
    private fun subscribeNavHandling():Disposable {
        return navView.itemSelections().subscribe { menuItem ->
            when (menuItem.itemId) {
                R.id.statistics_navigation_menu_item -> {
                    Intent(this@TasksActivity, StatisticsActivity::class.java)
                        .also { startActivity(it) }
                }
            }
            menuItem.isChecked = false
            drawerLayout.closeDrawers()
        }
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }
}