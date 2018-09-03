package com.kanawish.sample.mvi.model

import android.annotation.SuppressLint
import javax.inject.Inject
import javax.inject.Singleton


@SuppressLint("CheckResult")
@Singleton
class TaskEditorStore @Inject constructor() :
    ModelStore<TaskEditorState>(TaskEditorState.Editing(Task()))

