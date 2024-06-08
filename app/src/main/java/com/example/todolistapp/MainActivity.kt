package com.example.todolistapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.databinding.ActivityMainBinding
import io.realm.kotlin.types.RealmUUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.taskList.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(emptyList(), ::onTaskCheckChanged, ::onEditTask, ::onDeleteTask)
        binding.taskList.adapter = adapter

        viewModel.tasks.observe(this) { tasks ->
            adapter.updateData(tasks)
        }

        if (isNetworkAvailable()) {
            viewModel.fetchTasksFromApi()
        }

        binding.addTaskButton.setOnClickListener {
            showTaskDialog(null)
        }
    }

    private fun onTaskCheckChanged(task: Task, isChecked: Boolean) {
        viewModel.updateTaskCompletionStatus(task, isChecked)
    }

    private fun onEditTask(task: Task) {
        showTaskDialog(task)
    }

    private fun onDeleteTask(task: Task) {
        viewModel.deleteTask(task)
    }

    private fun showTaskDialog(task: Task?) {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_task, null)
        val titleInput = dialogLayout.findViewById<EditText>(R.id.task_title_input)
        val descriptionInput = dialogLayout.findViewById<EditText>(R.id.task_description_input)

        if (task != null) {
            titleInput.setText(task.title)
            descriptionInput.setText(task.description)
        }

        AlertDialog.Builder(this)
            .setTitle(if (task == null) "New Task" else "Edit Task")
            .setView(dialogLayout)
            .setPositiveButton(if (task == null) "Add" else "Update") { _, _ ->
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()
                if (title.isNotBlank()) {
                    if (task == null) {
                        val newTask = Task().apply {
                            id = RealmUUID.random().toString()
                            this.title = title
                            this.description = description
                            isCompleted = false
                        }
                        viewModel.addOrUpdateTask(newTask)
                    } else {
                        viewModel.updateTaskProperties(task, title, description)
                    }
                } else {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
}