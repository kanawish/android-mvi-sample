package com.kanawish.sample.mvi.view.tasks

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.TasksIntentFactory
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TasksModelStore
import com.kanawish.sample.mvi.model.TasksState
import com.kanawish.sample.mvi.view.StateSubscriber
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class TasksAdapter @Inject constructor(
    private val layoutInflater: LayoutInflater,
    private val tasksModelStore: TasksModelStore,
    private val tasksIntent: TasksIntentFactory
) : RecyclerView.Adapter<TaskViewHolder>(),
    StateSubscriber<TasksState> {

    private lateinit var filteredTasks: List<Task>

    private val disposables = CompositeDisposable()

    init {
        setHasStableIds(true)
    }

    override fun Observable<TasksState>.subscribeToState(): Disposable {
        return map(TasksState::filteredTasks)
            .distinctUntilChanged()
            .subscribe { updatedTasks ->
                filteredTasks = updatedTasks
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflatedView = layoutInflater.inflate(R.layout.task_item, parent, false)
        // NOTE: Another good 'special case' where the events are coming from ViewHolder, and...
        return TaskViewHolder(inflatedView).apply {
            events().subscribe(tasksIntent::process)
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(filteredTasks[position])
    }

    override fun getItemCount(): Int = filteredTasks.size

    // NOTE: ... the state changes are the concern of this parent TaskAdapter
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        disposables += tasksModelStore.modelState().subscribeToState()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }

}