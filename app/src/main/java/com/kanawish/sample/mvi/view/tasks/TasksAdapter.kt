package com.kanawish.sample.mvi.view.tasks

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.IntentMapper
import com.kanawish.sample.mvi.model.Model
import com.kanawish.sample.mvi.model.Task
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TasksAdapter @Inject constructor(
        private val inflater: LayoutInflater,
        private val intentMapper: IntentMapper,
        val model: Model
) : RecyclerView.Adapter<TasksViewHolder>() {

    var tasks: List<Task> = emptyList()

    // TODO: Validate this assumption is correct.
    // We rely on garbage collection of Adapter here.
    private val disposable: Disposable

    init {
        setHasStableIds(true)

        // NOTE: Not keeping disposable on hand would likely result in stream being garbage collected? (Validate)
        // FIXME validating Unless the lambda below captures a ref to task adapter...?
        // If this stayed alive, it means we'd leak eventually, right?
        disposable = model.tasks()
                .subscribe { newTasks ->
                    Timber.i("TaskAdapter received new tasks list of size ${newTasks.size}.")
                    tasks = newTasks

                    // Simplistic approach, forces a rebind for all visible viewHolders.
                    notifyDataSetChanged()
                }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TasksViewHolder {
        return TasksViewHolder(inflater.inflate(R.layout.task_item, parent, false), intentMapper::accept)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun onViewRecycled(holder: TasksViewHolder) {
        holder.unbind()
    }

    override fun getItemId(i: Int): Long {
        return tasks[i].id.toLong()
    }

    // Following can be useful if your ids are UUIDs.
    private fun uuidStringToLong(uuidString: String): Long = UUID.fromString(uuidString).mostSignificantBits and Long.MAX_VALUE
}