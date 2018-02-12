package com.kanawish.sample.mvi.view.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.support.v4.widget.refreshing
import com.jakewharton.rxbinding2.view.actionViewEvents
import com.jakewharton.rxbinding2.widget.textRes
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.IntentMapper
import com.kanawish.sample.mvi.intent.toViewEvent
import com.kanawish.sample.mvi.model.FilterType
import com.kanawish.sample.mvi.model.Model
import com.kanawish.sample.mvi.model.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.tasks_frag.filteringLabelTV
import kotlinx.android.synthetic.main.tasks_frag.noTasksIconIV
import kotlinx.android.synthetic.main.tasks_frag.noTasksLL
import kotlinx.android.synthetic.main.tasks_frag.noTasksMainTV
import kotlinx.android.synthetic.main.tasks_frag.swipeRefreshLayout
import kotlinx.android.synthetic.main.tasks_frag.tasksLL
import kotlinx.android.synthetic.main.tasks_frag.tasksRV
import timber.log.Timber
import javax.inject.Inject

/**
 * Created on 2017-06-14.
 */
class TasksFragment : Fragment() {

    @Inject
    lateinit var viewModel: ViewModel

    @Inject
    lateinit var intentMapper: IntentMapper

    @Inject
    lateinit var tasksAdapter: TasksAdapter

    // Issue with this + synthetic accessors.
//    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val disposables = CompositeDisposable()

    companion object {
        fun newInstance(): TasksFragment {
            return TasksFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.tasks_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tasksRV.adapter = tasksAdapter

        // TODO Investigate issues with swipeRefreshLayout + RxBindings.
    }


    override fun onResume() {
        Timber.i("onResume()")
        super.onResume()

        // MODEL(INTENT)
        // TasksViewEvent -> Intent
        disposables += swipeRefreshLayout.refreshes()
                .toViewEvent { TasksViewEvent.RefreshTasksPulled }
                .subscribe(intentMapper::accept)

        // VIEW(MODEL)

        // Shows progress indicator if a sync is in progress, hide otherwise.
        disposables += viewModel.refreshing()
                .doOnNext { Timber.i("isRefreshing = $it") }
                .subscribe(swipeRefreshLayout.refreshing())

        // Filtering label
        disposables += viewModel.filterDescription()
                .subscribe(filteringLabelTV.textRes())

        // Flips between empty state and recyclerView
        disposables += viewModel.contentVisibility()
                .subscribe { (tasksLLVisibility, noTasksLLVisibility) ->
                    tasksLL.visibility = tasksLLVisibility
                    noTasksLL.visibility = noTasksLLVisibility
                }

        // Change empty state to reflect selected filter, *when needed only*
        disposables += viewModel.noTasksMap()
                .subscribe { (descriptionRes, iconRes) ->
                    noTasksMainTV.setText(descriptionRes)
                    noTasksIconIV.setImageResource(iconRes) // Could be improved here.
                }

    }

    override fun onPause() {
        Timber.i("onPause()")
        super.onPause()

        disposables.clear()
    }


    // *** MENU HANDLING ***
    /**
     * NOTE: https://stackoverflow.com/questions/39146077/kotlin-android-extensions-and-menu
     *
     * Long story short - it is not possible to use KAE with Android Menus.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> intentMapper.accept(TasksViewEvent.ClearCompletedTasksClick)
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_refresh -> intentMapper.accept(TasksViewEvent.RefreshTasksPulled)
        }
        return true
    }

    fun showFilteringPopUpMenu() {
        val popup = PopupMenu(context!!, activity!!.findViewById(R.id.menu_filter))
        popup.menuInflater.inflate(R.menu.filter_tasks, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.active -> intentMapper.accept(TasksViewEvent.FilterTypeSelected(FilterType.ACTIVE))
                R.id.completed -> intentMapper.accept(TasksViewEvent.FilterTypeSelected(FilterType.COMPLETE))
                else -> intentMapper.accept(TasksViewEvent.FilterTypeSelected(FilterType.ANY))
            }
            true
        }

        popup.show()
    }

    // NOTE: We don't _always_ have to use Rx and subscriptions... using the following would get verbose.
    private fun rxIfy(menu: Menu) {
        disposables += menu.findItem(R.id.menu_clear).actionViewEvents()
                .toViewEvent { TasksViewEvent.ClearCompletedTasksClick }
                .subscribe(intentMapper::accept)
    }

    // NOTE: But eventually we could also build things like this...
    private fun Menu.bind(id: Int, event: TasksViewEvent): Disposable {
        return findItem(id).actionViewEvents()
                .toViewEvent { event }
                .subscribe(intentMapper::accept)
    }

    // NOTE: Giving us...
    private fun rxIfyTerse(menu: Menu) {
        // If you try this at home, keep an eye on menus odd lifecycle.
        disposables += menu.bind(R.id.menu_clear, TasksViewEvent.ClearCompletedTasksClick)
    }

}