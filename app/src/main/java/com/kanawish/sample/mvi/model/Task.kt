package com.kanawish.sample.mvi.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created on 2017-05-25.
 *
 * TODO: DB Task vs regular immutable Task.
 */
@Entity(tableName = "task")
data class Task(
        @PrimaryKey var id: String = UUID.randomUUID().toString(),
        var title: String = "New Task",
        var description: String = "",
        var completed: Boolean = false
)

fun uuidStringToLong(uuidString:String) : Long = UUID.fromString(uuidString).mostSignificantBits and Long.MAX_VALUE