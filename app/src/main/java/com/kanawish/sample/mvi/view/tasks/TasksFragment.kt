package com.kanawish.sample.mvi.view.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.jakewharton.rxbinding2.widget.textRes
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.TasksIntentFactory
import com.kanawish.sample.mvi.model.FilterType
import com.kanawish.sample.mvi.model.SyncState
import com.kanawish.sample.mvi.model.TasksModelStore
import com.kanawish.sample.mvi.model.TasksState
import com.kanawish.sample.mvi.view.EventObservable
import com.kanawish.sample.mvi.view.StateSubscriber
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.tasks_frag.filteringLabelTextView
import kotlinx.android.synthetic.main.tasks_frag.noTasksLinearLayout
import kotlinx.android.synthetic.main.tasks_frag.noTasksMainTextView
import kotlinx.android.synthetic.main.tasks_frag.swipeRefreshLayout
import kotlinx.android.synthetic.main.tasks_frag.view.tasksRecyclerView
import timber.log.Timber
import javax.inject.Inject

/**
 * This fragment hold the UI for the list of tasks you can check off.
 */
class TasksFragment : Fragment(), StateSubscriber<TasksState>, EventObservable<TasksViewEvent> {

    @Inject lateinit var tasksModelStore: TasksModelStore
    @Inject lateinit var tasksIntentFactory: TasksIntentFactory

    private val disposables = CompositeDisposable()
    private val menuDisposables = CompositeDisposable() // NOTE: We work around Android 'menu lifecycle'

    @Inject lateinit var tasksAdapter: TasksAdapter

    override fun Observable<TasksState>.subscribeToState(): Disposable {
        return CompositeDisposable(
                map { it.syncState }.ofType<SyncState.ERROR>().map { it.throwable }.subscribe(Timber::e),
                map { it.syncState is SyncState.PROCESS }.subscribe(swipeRefreshLayout::setRefreshing),
                map { it.filter.name }.subscribe(filteringLabelTextView.text()),
                map { it.filteredTasks().isEmpty() }.subscribe(noTasksLinearLayout.visibility()),
                map { tasksState ->
                    when (tasksState.filter) {
                        FilterType.ANY -> R.string.no_tasks_all
                        FilterType.ACTIVE -> R.string.no_tasks_active
                        FilterType.COMPLETE -> R.string.no_tasks_completed
                    }
                }.subscribe(noTasksMainTextView.textRes())
        )
    }

    override fun events(): Observable<TasksViewEvent> {
        return swipeRefreshLayout.refreshes().map { TasksViewEvent.RefreshTasksSwipe }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.tasks_frag, container, false).also { view ->
            view.tasksRecyclerView.adapter = tasksAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        disposables += events().subscribe(tasksIntentFactory::process)
        disposables += tasksModelStore.modelState().subscribeToState()
    }

    // NOTE: Example how `disposables` can help us work around edges cases in the Android Lifecycle.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)

        // onCreateOptionsMenu() is only called on menu creation.
        // This means we need to create an ViewEvent source, convert to Intent and subscribeToState the model.
        val menuEvents = Observable.merge(
                menu.findItem(R.id.menu_filter).clicks().map { TasksViewEvent.FilterTypeClick },
                menu.findItem(R.id.menu_refresh).clicks().map { TasksViewEvent.RefreshTasksClick },
                menu.findItem(R.id.menu_clear).clicks().map { TasksViewEvent.ClearCompletedClick }
        )

        // Add the new subscription to disposables, as needed.
        menuDisposables += menuEvents.subscribe(tasksIntentFactory::process)
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        menuDisposables.dispose()
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

}