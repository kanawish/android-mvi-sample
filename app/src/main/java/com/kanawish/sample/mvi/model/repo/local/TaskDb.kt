package com.kanawish.sample.mvi.model.repo.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.kanawish.sample.mvi.model.Task
import javax.inject.Singleton

/**
 * Created on 2017-06-14.
 *
 */
@Singleton
@Database(entities = arrayOf(Task::class), version = 1)
abstract class TaskDb : RoomDatabase() {
    abstract fun taskDao():TaskDao
}