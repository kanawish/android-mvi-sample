package com.kanawish.sample.mvi.view.statistics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.util.replaceFragmentInActivity
import com.kanawish.sample.mvi.util.setupActionBar

/**
 * Activity houses the Toolbar, the nav UI, the FAB and the fragment for stats.
 */
class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as StatisticsFragment?
                ?: StatisticsFragment().also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}