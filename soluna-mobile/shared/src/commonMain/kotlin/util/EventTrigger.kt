package com.russhwolf.soluna.mobile.util

@Suppress("DataClassPrivateConstructor")
data class EventTrigger<T : Any> private constructor(private var data: T?) {
    companion object {
        fun <T : Any> empty() = EventTrigger(null as T?)
        fun <T : Any> create(data: T) = EventTrigger(data)
        fun create() = EventTrigger(Unit)
    }

    fun consume(consumer: (T) -> Unit = {}): T? {
        val out = data
        data = null
        return out?.also(consumer)
    }
}
