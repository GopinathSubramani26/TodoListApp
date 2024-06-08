package com.example.todolistapp
import retrofit2.http.GET

interface TaskApiService {
    @GET("todos")
    suspend fun getTasks(): TaskResponse
}