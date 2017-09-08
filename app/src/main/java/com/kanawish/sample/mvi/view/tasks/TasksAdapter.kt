package com.kanawish.sample.mvi.view.tasks

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.repo.TaskRepo
import com.kanawish.sample.mvi.model.uuidStringToLong
import javax.inject.Inject

class TasksAdapter @Inject constructor(val inflater: LayoutInflater, val taskRepo: TaskRepo) : RecyclerView.Adapter<TasksViewHolder>() {

    var tasks: List<Task> = emptyList()

    // TODO: Hook up this disposable to be part of the overall activity lifecycle
    val disposable = taskRepo.tasks().subscribe {
        tasks = it
        notifyDataSetChanged()
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TasksViewHolder {
        return TasksViewHolder(inflater.inflate(R.layout.task_item, parent, false))
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.bind(tasks[position], taskRepo::process)
    }

    override fun onViewRecycled(holder: TasksViewHolder) {
        holder.unbind()
    }

    override fun getItemId(i: Int): Long {
        return uuidStringToLong(tasks[i].id)
    }

}