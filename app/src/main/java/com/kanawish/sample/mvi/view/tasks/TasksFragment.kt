package com.kanawish.sample.mvi.view.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.support.v4.widget.refreshing
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.toIntent
import com.kanawish.sample.mvi.model.Model
import com.kanawish.sample.mvi.model.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.tasks_frag.noTasksIconIV
import kotlinx.android.synthetic.main.tasks_frag.noTasksLL
import kotlinx.android.synthetic.main.tasks_frag.noTasksMainTV
import kotlinx.android.synthetic.main.tasks_frag.swipeRefreshLayout
import kotlinx.android.synthetic.main.tasks_frag.tasksLL
import kotlinx.android.synthetic.main.tasks_frag.tasksRV
import javax.inject.Inject

/**
 * Created on 2017-06-14.
 */
class TasksFragment : Fragment() {

    @Inject
    lateinit var viewModel: ViewModel

    // TODO: Replace with intent layer consumer?
    @Inject
    lateinit var model: Model

    @Inject
    lateinit var tasksAdapter: TasksAdapter

    private val disposables = CompositeDisposable()

    companion object {
        fun newInstance(): TasksFragment {
            return TasksFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tasks_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tasksRV.adapter = tasksAdapter
    }

    override fun onResume() {
        super.onResume()

        // MODEL(INTENT)
        // TasksViewEvent -> Intent
        disposables += swipeRefreshLayout.refreshes()
                .map<TasksViewEvent> { TasksViewEvent.RefreshTasksPulled }
                .toIntent()
                .subscribe(model::accept)

        // VIEW(MODEL)

        // Shows progress indicator if a sync is in progress, hide otherwise.
        disposables += viewModel.refreshing().subscribe(swipeRefreshLayout.refreshing())

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
        super.onPause()

        disposables.dispose()
    }

}