package com.kanawish.sample.mvi.di

import android.app.Application
import com.kanawish.sample.mvi.model.BasicModel
import com.kanawish.sample.mvi.model.Model
import com.kanawish.sample.mvi.model.ViewModel
import toothpick.config.Module

class AppModule(appContext: Application) : Module() {

    init {
        bind(Model::class.java).to(BasicModel::class.java)
        bind(ViewModel::class.java)
    }
}