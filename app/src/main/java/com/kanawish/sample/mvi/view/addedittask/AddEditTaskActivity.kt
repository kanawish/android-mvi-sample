package com.kanawish.sample.mvi.view.addedittask

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.util.replaceFragmentInActivity
import com.kanawish.sample.mvi.util.setupActionBar

/**
 * Activity houses the Toolbar, a FAB and the fragment for adding/editing tasks.
 */
class AddEditTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
                ?: AddEditTaskFragment().also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}