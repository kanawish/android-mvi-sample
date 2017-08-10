package com.kanawish.sample.mvi.di

import android.app.Application
import android.arch.persistence.room.Room
import com.kanawish.sample.mvi.model.repo.TaskRepo
import com.kanawish.sample.mvi.model.repo.BasicTaskRepo
import com.kanawish.sample.mvi.model.repo.local.TaskDb
import toothpick.config.Module

class AppModule(appContext: Application) : Module() {

    init {
        bind(TaskDb::class.java).toInstance(Room.databaseBuilder(appContext, TaskDb::class.java, "taskDb").build())
        bind(TaskRepo::class.java).to(BasicTaskRepo::class.java)

        // TODO: Add bindings.
        // bind()
    }
}