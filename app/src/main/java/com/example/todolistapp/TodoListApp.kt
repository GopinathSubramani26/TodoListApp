package com.example.todolistapp

import android.app.Application
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class TodoListApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = RealmConfiguration.Builder(
            schema = setOf(Task::class)
        )
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.open(config)
    }
}

