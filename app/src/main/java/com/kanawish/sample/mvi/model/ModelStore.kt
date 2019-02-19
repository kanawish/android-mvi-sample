package com.kanawish.sample.mvi.model

import com.jakewharton.rxrelay2.PublishRelay
import com.kanawish.sample.mvi.intent.Intent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

open class ModelStore<S>(startingState: S) : Model<S> {

    private val intents = PublishRelay.create<Intent<S>>()

    private val store = intents
        .observeOn(AndroidSchedulers.mainThread())
        .scan(startingState) { oldState, intent -> intent.reduce(oldState) }
        .replay(1)
        .apply { connect() }

    /**
     * Model will receive intents to be processed via this function.
     *
     * ModelState is immutable. Processed intents will work much like `copy()`
     * and create a new (modified) modelState from an old one.
     */
    override fun process(intent: Intent<S>) = intents.accept(intent)

    /**
     * Observable stream of changes to ModelState
     *
     * Every time a modelState is replaced by a new one, this observable will
     * fire.
     *
     * This is what views will subscribe to.
     */
    override fun modelState(): Observable<S> = store
}