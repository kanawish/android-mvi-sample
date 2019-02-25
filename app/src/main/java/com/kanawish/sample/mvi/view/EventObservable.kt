package com.kanawish.sample.mvi.view

import io.reactivex.Observable

interface EventObservable<E> {
    fun events(): Observable<E>
}