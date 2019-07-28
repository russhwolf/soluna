package com.russhwolf.soluna.mobile.util

class EventTrigger<T : Any> private constructor(data: T?) {
    companion object {
        fun <T : Any> empty() = EventTrigger<T>(null as T?)
        fun <T : Any> create(data: T) = EventTrigger(data)
    }

    var data: T? = data
        private set

    fun consume() {
        data = null
    }
}
