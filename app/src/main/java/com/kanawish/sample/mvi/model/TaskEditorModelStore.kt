package com.kanawish.sample.mvi.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskEditorModelStore @Inject constructor() :
    // TODO: Change back initial state to CLOSED for final version.
    ModelStore<TaskEditorState>(TaskEditorState.Editing(
            Task(title = "Cheese", description = "Komijne Kaas"),true))
