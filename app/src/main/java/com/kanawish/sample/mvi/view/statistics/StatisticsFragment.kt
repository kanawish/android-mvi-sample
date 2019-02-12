package com.kanawish.sample.mvi.view.statistics

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.kanawish.sample.mvi.R
import kotlinx.android.synthetic.main.addtask_act.fab_edit_task_done

/**
 * Shows statistics for the app.
 */
class StatisticsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistics_frag, container, false)
    }

}