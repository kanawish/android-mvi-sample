package com.kanawish.sample.mvi.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksModelStore @Inject constructor() :
        ModelStore<TasksModelState>(
                TasksModelState(
                        emptyList(),
                        FilterType.ANY,
                        SyncState.IDLE
                )
        )