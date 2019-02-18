package com.kanawish.sample.mvi.view

import io.reactivex.Observable

/**
 * Events are exposed via an Observable<E> stream.
 */
interface EventObservable<E> {
    fun events(): Observable<E>
}