package com.kanawish.sample.mvi.intent

import io.reactivex.Observable

interface Intent<S> {
    fun reducers(): Observable<Reducer<S>>
}

typealias Reducer<S> = (S) -> S

// NOTE: Magic of extension functions, (T)->T and T.()->T interchangeable.
fun <T> intent(block: T.() -> T): Intent<T> = object : Intent<T> {
    override fun reducers(): Observable<Reducer<T>> = Observable.just(block)
}

/**
 * checkedIntent function creating a single-reducer intent. We use this to guard against
 * incoherent incoming ViewEvents.
 */
inline fun <reified S : T, reified T> checkedIntent(crossinline block: S.() -> T): Intent<T> =
    intent {
        (this as? S)?.block()
            ?: throw IllegalStateException("checkedReducer encountered an inconsistent State.")
    }