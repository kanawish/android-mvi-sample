package com.kanawish.sample.mvi.model

/**
 * Created on 2017-06-06.
 */
data class TaskRepoState(
        val tasks: List<Task> = emptyList(),
        val synchronizing: Boolean = false
)