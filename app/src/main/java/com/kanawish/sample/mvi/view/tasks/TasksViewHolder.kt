package com.kanawish.sample.mvi.view.tasks

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.Intent
import com.kanawish.sample.mvi.intent.toIntent
import com.kanawish.sample.mvi.model.Task
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class TasksViewHolder(
        itemView: View,
        private val intentConsumer: (Intent) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    // NOTE: I believe we can't use kotlin's synthetic accessors. (Double check)
    private val checkBox: CheckBox = itemView.findViewById(R.id.complete)
    private val titleView: TextView = itemView.findViewById(R.id.title)

    private val disposables = CompositeDisposable()

    private fun checkBoxChanges(task: Task): Observable<Intent> {
        return checkBox
                .checkedChanges()
                .skipInitialValue()
                .map<TasksViewEvent> { checked ->
                    TasksViewEvent.TaskCheckBoxClick(task, checked)
                }
                .toIntent()
    }

    private fun titleViewClicks(task: Task): Observable<Intent> {
        return titleView
                .clicks()
                .map<TasksViewEvent> { TasksViewEvent.TaskClick(task) }
                .toIntent()
    }

    // TODO: Evaluate performance costs of on bind/unbind.
    fun bind(task: Task) {
        // NOTE: This is important to avoid triggering intents on re-bind.
        disposables.clear()

        // Binds task after applying view changes.
        titleView.text = task.title
        checkBox.isChecked = task.completed

        disposables += checkBoxChanges(task).subscribe(intentConsumer)
        disposables += titleViewClicks(task).subscribe(intentConsumer)
    }

}