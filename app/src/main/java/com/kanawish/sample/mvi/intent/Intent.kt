package com.kanawish.sample.mvi.intent

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

interface Intent<S> {
    fun reducers(): Observable<Reducer<S>>
}

typealias Reducer<S> = (S) -> S

fun <S> singleReducerIntent(reducer: (S) -> S): Intent<S> {
    return object : Intent<S> {
        override fun reducers(): Observable<Reducer<S>> {
            return Observable.just(reducer)
        }
    }
}

fun <S> singleBlockIntent(block: S.() -> S): Intent<S> {
    return object : Intent<S> {
        override fun reducers(): Observable<Reducer<S>> {
            return Observable.just { old -> old.block() }
        }
    }
}

fun <S> intervalBlocksIntent(period: Long, vararg blocks: S.() -> S): Intent<S> {
    return object : Intent<S> {
        override fun reducers(): Observable<Reducer<S>> {
            return Observable.fromArray(*blocks)
                    .map { block -> { old: S -> old.block() } }
                    .zipWith(
                            Observable.interval(period, TimeUnit.SECONDS),
                            BiFunction { b, _ -> b }
                    )
        }
    }
}