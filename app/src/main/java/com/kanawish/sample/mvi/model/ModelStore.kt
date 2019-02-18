package com.kanawish.sample.mvi.model

import com.jakewharton.rxrelay2.PublishRelay
import com.kanawish.sample.mvi.intent.Intent
import com.kanawish.sample.mvi.intent.intent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observables.ConnectableObservable
import timber.log.Timber

open class ModelStore<S>(startingState: S) : Model<S> {

    private val intents = PublishRelay.create<Intent<S>>()

    private val store = intents
            .flatMap { it.reducers() }
            .observeOn(AndroidSchedulers.mainThread())
            .scan(startingState) { oldState, reducer -> reducer(oldState) }
            .replay(1)
            .apply { connect() }

    /**
     * Allows us to react to problems within the ModelStore.
     */
    private val internalDisposable = store.subscribe(::internalLogger, ::crashHandler)

    private fun internalLogger(state:S) = Timber.i("$state")

    private fun crashHandler(throwable: Throwable): Unit = throw throwable

    /**
     * Model will receive intents to be processed via this function.
     *
     * ModelState is immutable. Processed intents trigger modelState _transitions_.
     */
    override fun process(intent: Intent<S>) = intents.accept(intent)

    /**
     * Observable stream of changes to ModelState
     *
     * ModelState is immutable, and new modelState instances are emitted every
     * time a processed intent triggers a _transition_.
     */
    override fun modelState(): ConnectableObservable<S> = store

}

fun <S> ModelStore<S>.processIntent(block: S.() -> S) = process(intent(block))

