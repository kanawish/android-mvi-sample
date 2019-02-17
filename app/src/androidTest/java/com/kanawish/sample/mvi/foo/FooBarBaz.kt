package com.kanawish.sample.mvi.foo

import android.support.v4.app.Fragment
import com.jakewharton.rxbinding2.widget.checked
import com.jakewharton.rxbinding2.widget.text
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.view.StateSubscriber
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.addtask_frag.add_task_description
import kotlinx.android.synthetic.main.task_item.completeCheckBox

/**
 *
 * NOTE: Any dummy sample code will be added to the `.foo` package.
 *
 */
class FooSubscriberFragment() : Fragment(),
    StateSubscriber<Task> {
    override fun Observable<Task>.subscribeToState(): Disposable {
        // Bundles all the subscription disposables for easy management.
        return CompositeDisposable().also { d ->
            // Only update task description field when needed.
            d += map { it.description } // Only cares about description.
                .distinctUntilChanged() // Only initial value and future changes.
                .subscribe( add_task_description.text() ) // Only updates when relevant.

            // One time-initialization editable field.
            d += firstOrError() // Only gets current value.
                .map { it.completed } // Only care about the completed state.
                .subscribe( completeCheckBox.checked() ) // Will be set only once
        }
    }
}