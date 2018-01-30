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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class TasksViewHolder(itemView: View, intentConsumer: (Intent) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val checkBox: CheckBox = itemView.findViewById(R.id.complete)
    private val titleView: TextView = itemView.findViewById(R.id.title)

    private val disposables = CompositeDisposable()

    lateinit var boundTask: Task

    init {
        disposables += checkBox.checkedChanges()
                .map<TasksViewEvent> { TasksViewEvent.TaskCheckBoxClick(boundTask, it) }
                .toIntent()
                .subscribe(intentConsumer)

        disposables += titleView.clicks()
                .map<TasksViewEvent> { TasksViewEvent.TaskClick(boundTask) }
                .toIntent()
                .subscribe(intentConsumer)
    }

    fun bind(task: Task) {
        // Binds task after applying view changes.
        boundTask = task.apply {
            titleView.text = title
            checkBox.isChecked = completed
        }
    }

}