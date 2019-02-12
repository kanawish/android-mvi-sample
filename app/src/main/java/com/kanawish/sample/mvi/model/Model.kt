package com.kanawish.sample.mvi.model

import com.kanawish.sample.mvi.intent.Intent
import io.reactivex.observables.ConnectableObservable

interface Model<S> {
    /**
     * Model will receive intents to be processed via this function.
     *
     * ModelState is immutable. Processed intents trigger modelState _transitions_.
     */
    fun process(intent: Intent<S>)

    /**
     * Observable stream of changes to ModelState
     *
     * ModelState is immutable, and new modelState instances are emitted every
     * time a processed intent triggers a _transition_.
     */
    fun modelState(): ConnectableObservable<S>
}