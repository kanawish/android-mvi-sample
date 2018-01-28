package com.kanawish.sample.mvi.di

import android.app.Application
import com.kanawish.sample.mvi.model.BasicModel
import com.kanawish.sample.mvi.model.Model
import toothpick.config.Module

class AppModule(appContext: Application) : Module() {

    init {
        bind(Model::class.java).to(BasicModel::class.java)

        // TODO: Add bindings.
        // bind()
    }
}