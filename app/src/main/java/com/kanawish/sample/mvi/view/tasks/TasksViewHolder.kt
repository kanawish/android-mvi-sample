package com.kanawish.sample.mvi.view.tasks

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.AppIntent
import com.kanawish.sample.mvi.model.Task
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val checkBox: CheckBox = itemView.findViewById(R.id.complete)
    val titleView: TextView = itemView.findViewById(R.id.title)

    val disposables = CompositeDisposable()

    fun bind(task: Task, intentConsumer: (AppIntent) -> Unit) {

        with(task) {
            titleView.text = title
            checkBox.isChecked = completed
        }

        disposables.clear()

        disposables += checkBox.checkedChanges()
                .map { TasksViewEvent.TaskCheckBoxClick(task, it) }
                .compose(AppIntent.tasksViewEventTransformer)
                .subscribe(intentConsumer)

        disposables += titleView.clicks()
                .map { TasksViewEvent.TaskClick(task) }
                .compose(AppIntent.tasksViewEventTransformer)
                .subscribe(intentConsumer)
    }

    fun unbind() {
        disposables.clear()
    }

}