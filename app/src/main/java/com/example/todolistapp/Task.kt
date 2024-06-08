package com.example.todolistapp

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Task : RealmObject {
    @PrimaryKey
    var id: String = ""
    var title: String = ""
    var description: String = ""
    var isCompleted: Boolean = false
}

data class TaskResponse(
    val limit: Int,
    val skip: Int,
    val todos: List<Todo>,
    val total: Int
)

data class Todo(
    val completed: Boolean,
    val id: Int,
    val todo: String,
    val userId: Int
)
