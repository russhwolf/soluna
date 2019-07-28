package com.russhwolf.soluna.mobile.util

class EventTrigger<T : Any> private constructor(private var data: T?) {
    companion object {
        fun <T : Any> empty() = EventTrigger<T>(null as T?)
        fun <T : Any> create(data: T) = EventTrigger(data)
    }

    fun consume(): T? {
        val out = data
        data = null
        return out
    }
}
