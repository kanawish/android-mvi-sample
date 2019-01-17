package com.kanawish.sample.mvi.util

/**
 * A quick and dirty way to add to an immutable list.
 *
 * Consider using immutable list libraries in heavier use-cases.
 */
fun <T> List<T>.copyAdd(t:T):List<T> = toMutableList().apply { add(t) }

