package com.kanawish.sample.mvi.view.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.model.Task
import com.kanawish.sample.mvi.model.repo.TaskRepo
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Created on 2017-06-14.
 */
class TasksFragment : Fragment() {

    private lateinit var listAdapter: TasksAdapter

    private lateinit var noTasksView: View
    private lateinit var noTaskIcon: ImageView
    private lateinit var noTaskMainView: TextView
    private lateinit var noTaskAddView: TextView
    private lateinit var tasksView: LinearLayout
    private lateinit var filteringLabelView: TextView

    companion object {
        fun newInstance(): TasksFragment {
            return TasksFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter = TasksAdapter(emptyList(), itemEmitter = object : TaskItemEmitter {
            override fun onTaskClick(clickedTask: Task) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCompleteTaskClick(completedTask: Task) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onActivateTaskClick(activatedTask: Task) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun onResume() {
        super.onResume()
        // TODO: Bind to intent (intent observers the view as a source.)
    }

    override fun onPause() {
        super.onPause()
        // TODO: Unbind intent (intent disposes of observed view.)
    }

    interface TaskItemEmitter {

        fun onTaskClick(clickedTask: Task)

        fun onCompleteTaskClick(completedTask: Task)

        fun onActivateTaskClick(activatedTask: Task)
    }


    open class Test @Inject constructor(val taskRepo: TaskRepo) {
        val taskViewDisposables: MutableMap<View, Disposable> = hashMapOf()

        fun View.bind(binder: (View) -> Disposable) {
            taskViewDisposables[this]?.dispose()
            taskViewDisposables.put(this, binder(this))
        }

        fun View.unbind() {
            taskViewDisposables[this]?.dispose()
        }

        fun unbind() {
            taskViewDisposables.values.forEach(Disposable::dispose)
        }

    }

    private class TasksAdapter(tasks: List<Task>, private val itemEmitter: TaskItemEmitter) : BaseAdapter() {

        private var tasks: List<Task>? = null
        // TODO: Replace with observables on list...

        init {
            setList(tasks)
        }

        fun replaceData(tasks: List<Task>) {
            setList(tasks)
            notifyDataSetChanged()
        }

        private fun setList(tasks: List<Task>) {
            this.tasks = checkNotNull(tasks)
        }

        override fun getCount(): Int {
            return tasks!!.size
        }

        override fun getItem(i: Int): Task {
            return tasks!![i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        // FIXME this needs converting to idiomatic kotlin.
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val rowView = view ?: LayoutInflater.from(viewGroup.context).inflate(R.layout.task_item, viewGroup, false)
            val titleTV = rowView.findViewById(R.id.title) as TextView

/*
            rowView.bind(binder = {
                val completeCB = it.findViewById(R.id.complete) as CheckBox?

                CompositeDisposable().addAll(
                        titleTV
                )
            })
*/

            val task = getItem(i)

            titleTV.setText(task.title)

            val completeCB = rowView.findViewById(R.id.complete) as CheckBox

            // Active/completed task UI
            completeCB.isChecked = task.completed
            if (task.completed) {
                rowView.setBackgroundDrawable(viewGroup.context
                        .resources.getDrawable(R.drawable.list_completed_touch_feedback))
            } else {
                rowView.setBackgroundDrawable(viewGroup.context
                        .resources.getDrawable(R.drawable.touch_feedback))
            }

            completeCB.setOnClickListener { _ ->
                if (!task.completed) {
                    itemEmitter.onCompleteTaskClick(task)
                } else {
                    itemEmitter.onActivateTaskClick(task)
                }
            }

            rowView.setOnClickListener { _ -> itemEmitter.onTaskClick(task) }

            return rowView
        }
    }

}