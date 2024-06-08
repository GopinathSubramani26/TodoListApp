package com.example.todolistapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskCheckChanged: (Task, Boolean) -> Unit,
    private val onEditTask: (Task) -> Unit,
    private val onDeleteTask: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.task_title)
        val taskDescription: TextView = itemView.findViewById(R.id.task_description)
        val taskCompleted: CheckBox = itemView.findViewById(R.id.task_completed)
        val editTaskButton: ImageButton = itemView.findViewById(R.id.edit_task_button)
        val deleteTaskButton: ImageButton = itemView.findViewById(R.id.delete_task_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitle.text = task.title
        holder.taskDescription.text = task.description
        holder.taskCompleted.setOnCheckedChangeListener(null)
        holder.taskCompleted.isChecked = task.isCompleted
        holder.taskCompleted.setOnCheckedChangeListener { _, isChecked ->
            onTaskCheckChanged(task, isChecked)
        }
        holder.editTaskButton.setOnClickListener {
            onEditTask(task)
        }
        holder.deleteTaskButton.setOnClickListener {
            onDeleteTask(task)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateData(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}





