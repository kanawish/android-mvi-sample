package com.kanawish.sample.mvi.view.tasks

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.Model
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TasksAdapter @Inject constructor(val inflater: LayoutInflater, val model: Model) : RecyclerView.Adapter<TasksViewHolder>() {

    var tasks: List<Task> = emptyList()

    // We rely on garbage collection of Adapter to cause natural
//    private val disposable:Disposable

    init {
        setHasStableIds(true)

        // NOTE: Not keeping disposable on hand would likely result in stream being garbage collected? (Validate)
        // FIXME validating Unless the lambda below captures a ref to task adapter...?
        // If this stayed alive, it means we'd leak eventually, right?
//        disposable =
                model.tasks().subscribe {
            Timber.i("TaskAdapter received new tasks list of size ${tasks.size}.")
            tasks = it
            // Simplistic approach, forces a rebind for all visible viewHolders.
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = tasks.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TasksViewHolder {
        return TasksViewHolder(inflater.inflate(R.layout.task_item, parent, false), model::accept )
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemId(i: Int): Long {
        return uuidStringToLong(tasks[i].id)
    }

    // Could eventually lead to id collisions?
    private fun uuidStringToLong(uuidString: String): Long = UUID.fromString(uuidString).mostSignificantBits and Long.MAX_VALUE
}