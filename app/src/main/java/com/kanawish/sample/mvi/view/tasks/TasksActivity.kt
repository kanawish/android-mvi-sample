package com.kanawish.sample.mvi.view.tasks

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.design.widget.itemSelections
import com.jakewharton.rxbinding2.view.clicks
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.model.Model
import com.kanawish.sample.mvi.util.ActivityUtils
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.tasks_act.*
import javax.inject.Inject


/**
 * Created on 2017-06-14.
 *
 * TODO: refactor to idiomatic kotlin.
 */
class TasksActivity : AppCompatActivity() {

    @Inject
    lateinit var model: Model

    val disposables = CompositeDisposable()

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

        addTaskFAB.setImageResource(R.drawable.ic_add)

        // TODO: Find the problem with fragment lifecycle in this current sample...
        var tasksFragment: TasksFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as? TasksFragment ?:
                TasksFragment.newInstance().also {
                    ActivityUtils.addFragmentToActivity(
                            supportFragmentManager, it, R.id.contentFrame)
                }

    }

    override fun onResume() {
        super.onResume()

        disposables += addTaskFAB
                .clicks()
                .subscribe { startActivity(Intent(this, AddEditTaskActivity::class.java)) }

        navView?.itemSelections()?.subscribe(this::handleItemSelection)?.let(disposables::add)

    }

    private fun handleItemSelection(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.list_navigation_menu_item -> {
            }
            R.id.statistics_navigation_menu_item -> {
                //                        val intent = Intent(this@TasksActivity, StatisticsActivity::class.java)
                //                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                //                        startActivity(intent)
            }
        }
        menuItem.isChecked = true
        drawerLayout.closeDrawers()
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
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

    private fun setupDrawerContent(navigationView: NavigationView) {

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
    }

}