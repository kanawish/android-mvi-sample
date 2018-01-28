package com.kanawish.sample.mvi.view.tasks

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.model.Model
import kotlinx.android.synthetic.main.tasks_frag.*
import toothpick.Toothpick
import javax.inject.Inject

/**
 * Created on 2017-06-14.
 */
class TasksFragment : Fragment() {

    @Inject lateinit var model: Model

    @Inject lateinit var tasksAdapter: TasksAdapter

    companion object {
        fun newInstance(): TasksFragment {
            return TasksFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Toothpick.inject(this, Toothpick.openScope(context))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tasks_frag, container, false)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        tasks_list.adapter = tasksAdapter
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
