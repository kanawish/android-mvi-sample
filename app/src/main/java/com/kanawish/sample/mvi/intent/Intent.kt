package com.kanawish.sample.mvi.intent

import io.reactivex.Observable

interface Intent<T> {
    fun reducers(): Observable<Reducer<T>>
}

typealias Reducer<T> = (T) -> T

/**
 * DSL function to build single-reducer `Intent<T>`.
 *
 * NOTE: Magic of extension functions, (T)->T and T.()->T interchangeable.
 */
fun <T> intent(block: T.() -> T): Intent<T> = object : Intent<T> {
    override fun reducers(): Observable<Reducer<T>> = Observable.just(block)
}

/**
 * By delegating work to other models, repositories or services, we
 * end up with situations where we don't need to update our ModelStore
 * state until the delegated work completes.
 *
 * Use the `sideEffect { }` DSL function for those situations.
 */
fun <T> sideEffect(block:T.()->Unit):Intent<T> = object : Intent<T> {
    override fun reducers(): Observable<Reducer<T>> {
        val reducer : Reducer<T> =  { it.apply(block) }
        return Observable.just(reducer)
    }
}