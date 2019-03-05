package com.kanawish.sample.mvi.view

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Consumers of a given state source often need to create fine-grained subscriptions
 * to control performance and frequency of updates.
 */
interface StateSubscriber<S> {
    fun Observable<S>.subscribeToState(): Disposable
}