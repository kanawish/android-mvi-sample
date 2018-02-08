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
import com.kanawish.sample.mvi.model.SyncState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.tasks_frag.noTasks
import kotlinx.android.synthetic.main.tasks_frag.refresh_layout
import kotlinx.android.synthetic.main.tasks_frag.tasks_list
import timber.log.Timber
import javax.inject.Inject

/**
 * Created on 2017-06-14.
 */
class TasksFragment : Fragment() {

    @Inject
    lateinit var model: Model

    @Inject
    lateinit var tasksAdapter: TasksAdapter

    val disposables = CompositeDisposable()

    companion object {
        fun newInstance(): TasksFragment {
            return TasksFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.tasks_frag, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tasks_list.adapter = tasksAdapter
    }

    override fun onResume() {
        super.onResume()

        // TODO: Add timeout handling?
        // MODEL(INTENT)
        // TasksViewEvent -> Intent
        disposables += refresh_layout
                .refreshes()
                .map<TasksViewEvent> { TasksViewEvent.RefreshTasksPulled }
                .doOnNext { Timber.i("TasksViewEvent.RefreshTasksPulled") }
                .toIntent()
                .subscribe(model::accept)

        // VIEW(MODEL)
        // Show progress indicator if a sync is in progress, hide otherwise.
        disposables += model.syncState()
                .map { it is SyncState.PROCESS }
                .doOnNext { Timber.i("SyncState is PROCESS? $it") }
                .subscribe(refresh_layout.refreshing())

        disposables += model.tasks()
                // TODO: Add ViewModel concept
                .map {
                    if (it.size > 0) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
                .subscribe { noTasks.visibility = it }

        // TODO: Bind to intent (intent observers the view as a source.)
    }

    override fun onPause() {
        super.onPause()
        // TODO: Unbind intent (intent disposes of observed view.)
    }

}