package com.kanawish.sample.mvi.model

import android.annotation.SuppressLint
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("CheckResult")
@Singleton
class TaskEditorModelStore @Inject constructor() :
    ModelStore<TaskEditorState>(TaskEditorState.Editing(
            Task(title="Cheese", description = "Komijne Kaas"),true)
    )