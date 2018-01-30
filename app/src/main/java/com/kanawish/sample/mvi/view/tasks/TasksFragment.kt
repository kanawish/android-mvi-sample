package com.kanawish.sample.mvi.view.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.toIntent
import com.kanawish.sample.mvi.model.Model
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.tasks_frag.refresh_layout
import kotlinx.android.synthetic.main.tasks_frag.tasks_list
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
        disposables += refresh_layout
                .refreshes()
                .map<TasksViewEvent> { TasksViewEvent.RefreshTasksPulled }
                .toIntent()
                .subscribe(model::accept)
    }

    override fun onResume() {
        super.onResume()

        // TODO: Bind to intent (intent observers the view as a source.)
    }

    override fun onPause() {
        super.onPause()
        // TODO: Unbind intent (intent disposes of observed view.)
    }

}