package com.kanawish.sample.mvi.intent

/**
 * Created on 2017-07-10.
 */

inline fun <T> List<T>.copy(mutatorBlock: MutableList<T>.() -> Unit): List<T> {
    return toMutableList().apply(mutatorBlock)
}
