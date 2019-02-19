package com.kanawish.sample.mvi.intent

interface Intent<T> {
    fun reduce(oldState: T): T
}