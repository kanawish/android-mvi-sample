package com.kanawish.sample.mvi.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksModelStore @Inject constructor() :
        ModelStore<TasksState>(
                TasksState(
                        emptyList(),
                        FilterType.ANY,
                        SyncState.IDLE
                )
        )