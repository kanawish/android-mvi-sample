package com.kanawish.sample.mvi.intent

interface Intent<T> {
    fun reduce(oldState: T): T
}

/**
 * DSL function to help build intents from code blocks.
 *
 * NOTE: Magic of extension functions, (T)->T and T.()->T interchangeable.
 */
fun <T> intent(block: T.() -> T) : Intent<T> = object : Intent<T> {
    override fun reduce(oldState: T): T = block(oldState)
}