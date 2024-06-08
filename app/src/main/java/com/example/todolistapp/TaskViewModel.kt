package com.example.todolistapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks
    private lateinit var realm: Realm

    init {
        initializeRealm()
        loadTasks()
    }

    private fun initializeRealm() {
        val config = RealmConfiguration.Builder(schema = setOf(Task::class))
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.open(config)
    }

    private fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val results = realm.query<Task>().find()
            withContext(Dispatchers.Main) {
                _tasks.value = results
            }
        }
    }

    fun addOrUpdateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val existingTask = query<Task>("id == $0", task.id).first().find()
                if (existingTask != null) {
                    // Update existing task
                    findLatest(existingTask)?.apply {
                        title = task.title
                        description = task.description
                        isCompleted = task.isCompleted
                    }
                } else {
                    copyToRealm(task)
                }
            }
            loadTasks()
        }
    }

    fun updateTaskCompletionStatus(task: Task, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                findLatest(task)?.apply {
                    isCompleted = isChecked
                }
            }
            loadTasks()
        }
    }

    fun updateTaskProperties(task: Task, newTitle: String, newDescription: String) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                findLatest(task)?.apply {
                    title = newTitle
                    description = newDescription
                }
            }
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val foundTask = query<Task>("id == $0", task.id).first().find()
                foundTask?.let { delete(it) }
            }
            loadTasks()
        }
    }

    fun fetchTasksFromApi() {
        viewModelScope.launch {
            try {
                val tasksResponse = withContext(Dispatchers.IO) {
                    NetworkModule.api.getTasks()
                }
                val tasks = tasksResponse.todos.map { todo ->
                    Task().apply {
                        id = todo.id.toString()
                        title = todo.todo
                        description = ""
                        isCompleted = todo.completed
                    }
                }
                addOrUpdateTasks(tasks)
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error fetching tasks: ${e.message}")
                loadTasks()
            }
        }
    }

    private fun addOrUpdateTasks(tasks: List<Task>) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                tasks.forEach { task ->
                    val existingTask = query<Task>("id == $0", task.id).first().find()
                    if (existingTask != null) {
                        findLatest(existingTask)?.apply {
                            title = task.title
                            description = task.description
                            isCompleted = task.isCompleted
                        }
                    } else {
                        copyToRealm(task)
                    }
                }
            }
            loadTasks()
        }
    }
}