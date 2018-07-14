package com.kanawish.sample.mvi.intent

import io.reactivex.Observable

interface Intent<S> {
    fun reducers(): Observable<Reducer<S>>
}

typealias Reducer<S> = (S)->S

fun <S> singleIntent(reducer:(S) -> S):Intent<S> {
    return object : Intent<S> {
        override fun reducers(): Observable<Reducer<S>> {
            return Observable.just(reducer)
        }
    }
}