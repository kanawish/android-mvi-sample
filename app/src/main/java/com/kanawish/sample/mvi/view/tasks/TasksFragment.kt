package com.kanawish.sample.mvi.view.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.text
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.EditorIntentBuilder
import com.kanawish.sample.mvi.intent.toIntent
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.TaskEditorStore
import com.kanawish.sample.mvi.model.TasksModelState
import com.kanawish.sample.mvi.model.TasksModelStore
import com.kanawish.sample.mvi.view.ViewContract
import com.kanawish.sample.mvi.view.addedittask.AddEditTaskViewEvent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.task_item.view.complete
import kotlinx.android.synthetic.main.task_item.view.title
import kotlinx.android.synthetic.main.tasks_frag.filteringLabelTV
import kotlinx.android.synthetic.main.tasks_frag.noTasksLL
import kotlinx.android.synthetic.main.tasks_frag.swipeRefreshLayout
import kotlinx.android.synthetic.main.tasks_frag.view.tasksRV
import javax.inject.Inject

/**
 * This fragment hold the UI for the list of tasks you can check off.
 */
class TasksFragment : Fragment() {

    @Inject lateinit var tasksStore: TasksModelStore

    @Inject lateinit var tasksAdapter: TasksAdapter

    private val tasksContract = object : ViewContract<TasksViewEvent, TasksModelState> {
        override fun Observable<TasksModelState>.subscribeView(): Disposable {

            return subscribe {
                tasksStore.modelState().map { it.filter.name }.subscribe(filteringLabelTV.text())
                tasksStore.modelState().map { it.tasks.isEmpty() }.subscribe(noTasksLL.visibility())
                // Ordering(?), empty state...
            }
        }

        override fun events(): Observable<TasksViewEvent> {
            return swipeRefreshLayout.refreshes().map { TasksViewEvent.RefreshTasksSwipe }
        }
    }

    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.tasks_frag, container, false).also { view ->
            view.tasksRV.adapter = tasksAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)

        // onCreateOptionsMenu() is only called on menu creation.
        // This means we need to create an ViewEvent source, convert to Intent and subscribe the model.
        val menuEvents = Observable.merge(
                menu.findItem(R.id.menu_filter).clicks().map { TasksViewEvent.FilterTypeClick },
                menu.findItem(R.id.menu_refresh).clicks().map { TasksViewEvent.RefreshTasksClick },
                menu.findItem(R.id.menu_clear).clicks().map { TasksViewEvent.ClearCompletedClick }
        )
        // Add the new subscription to disposables, as needed.
        disposables += menuEvents.toIntent().subscribe(tasksStore::process)
    }

    override fun onPause() {
        super.onPause()
        disposables += tasksContract.events().toIntent().subscribe(tasksStore::process)
        disposables += tasksContract.run {
            tasksStore.modelState().subscribeView()
        }
    }

    override fun onResume() {
        super.onResume()
        disposables.clear()
    }

    class TasksAdapter @Inject constructor(
            private val layoutInflater: LayoutInflater,
            private val tasksStore: TasksModelStore,
            private val editorIntentBuilder: EditorIntentBuilder,
            private val editorStore: TaskEditorStore
    ) : RecyclerView.Adapter<TaskViewHolder>() {

        private lateinit var tasks: List<Task>

        private val disposables = CompositeDisposable()

        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            return TaskViewHolder(layoutInflater.inflate(R.layout.task_item, parent, false))
                    .also { viewHolder ->
                        // NOTE: Moving to 'viewEvent' consumer subscriptions would also be a good option.
                        disposables += viewHolder.taskCheckedChanges().toIntent().subscribe(tasksStore::process)
                        disposables += viewHolder.titleClicks().toIntent(editorIntentBuilder).subscribe(editorStore::process)
                    }
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.bind(tasks[position])
        }

        override fun getItemCount(): Int = tasks.size

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            // We want to learn of changes to the tasks list.
            disposables += tasksStore
                    .modelState()
                    .map(TasksModelState::tasks)
                    .distinctUntilChanged()
                    .subscribe {
                        tasks = it
                        notifyDataSetChanged()
                    }
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
            disposables.clear()
        }

    }

    /**
     * NOTE: ViewContract is really more a 'documenting' interface in this app.
     *
     * In the interest of simplicity, we drop the `subscriptionView()` approach, and
     * only bind on the tasks list updates.
     *
     * You could imagine scenarios where a ViewHolder points to a "live" source of
     * data. In those cases, binding to an Observable would be a good approach.
     */
    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var currentTask: Task

        fun bind(task: Task) {
            currentTask = task
            itemView.title.text = task.title
            itemView.complete.isChecked = task.completed
        }

        fun taskCheckedChanges(): Observable<TasksViewEvent> {
            return itemView.complete.checkedChanges().skipInitialValue().map {
                TasksViewEvent.CompleteTaskClick(currentTask, it)
            }
        }

        fun titleClicks(): Observable<AddEditTaskViewEvent> {
            return itemView.title.clicks().map {
                AddEditTaskViewEvent.EditTaskClick(currentTask)
            }
        }

    }
}
