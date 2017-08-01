package com.kanawish.sample.mvi.model.repo.local

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.kanawish.sample.mvi.model.Task

/**
 * Created on 2017-06-14.
 */
@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun selectTasks(): List<Task>

    @Query("SELECT * FROM task WHERE id = :arg0")
    fun selectTask(id: String): Task

    @Insert(onConflict = REPLACE)
    fun insertTask(task: Task)

    @Update(onConflict = REPLACE)
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Delete
    fun deleteTasks(tasks: List<Task>)

}