package com.kanawish.sample.mvi.view.tasks

import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.design.widget.NavigationView
import android.support.test.espresso.IdlingResource
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.util.ActivityUtils
import com.kanawish.sample.mvi.util.EspressoIdlingResource
import kotlinx.android.synthetic.main.tasks_act.*


/**
 * Created on 2017-06-14.
 *
 * TODO: refactor to idiomatic kotlin.
 */
class TasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        // Set up the toolbar.
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the navigation drawer.
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)

        if (navView != null) {
            setupDrawerContent(navView)
        }

        var tasksFragment: TasksFragment =
                supportFragmentManager.findFragmentById(R.id.contentFrame) as? TasksFragment ?:
                        TasksFragment.newInstance().also {
                            ActivityUtils.addFragmentToActivity(
                                    supportFragmentManager, it, R.id.contentFrame)
                        }

        // TODO: Binding with MVI uni-dir flow. Probably will happen at start-stop stage.


        // Load previously saved state, if available.
        // TODO: TasksFilterType should be persisted at our model layer...

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) =
            navigationView.setNavigationItemSelectedListener({ menuItem ->
                when (menuItem.itemId) {
                    R.id.list_navigation_menu_item -> {
                        // Nothing to do, we're on the list screen already.
                    }
                    R.id.statistics_navigation_menu_item -> {
                        // TODO: Re-enable once that screen is available.
/*
                        val intent = Intent(this@TasksActivity, StatisticsActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
*/
                    }
                }

                // Close the navigation drawer when an item is selected.
                menuItem.isChecked = true
                drawerLayout.closeDrawers()
                true
            })

    // TODO: Review this when implementing tests...
    @VisibleForTesting
    fun getCountingIdlingResource(): IdlingResource {
        return EspressoIdlingResource.idlingResource
    }

}