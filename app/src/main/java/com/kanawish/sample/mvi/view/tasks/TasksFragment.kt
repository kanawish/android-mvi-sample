package com.kanawish.sample.mvi.view.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.model.TasksModelStore
import kotlinx.android.synthetic.main.tasks_frag.view.tasksRecyclerView
import javax.inject.Inject

/**
 * This fragment hold the UI for the list of tasks you can check off.
 */
class TasksFragment : Fragment() {

    @Inject lateinit var tasksModelStore: TasksModelStore

    @Inject lateinit var tasksAdapter: TasksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater
            .inflate(R.layout.tasks_frag, container, false)
            .also { view ->
                view.tasksRecyclerView.adapter = tasksAdapter
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

}