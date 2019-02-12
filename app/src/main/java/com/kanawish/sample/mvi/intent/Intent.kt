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