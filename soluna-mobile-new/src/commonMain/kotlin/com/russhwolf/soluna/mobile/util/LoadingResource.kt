package com.russhwolf.soluna.mobile.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProduceStateScope
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay

sealed interface LoadingResource<out T> {
    data object Loading : LoadingResource<Nothing>
    data class Value<T>(val value: T) : LoadingResource<T>
}

@Composable
fun <T> produceLoadingResourceState(
    producer: suspend ProduceStateScope<LoadingResource<T>>.() -> Unit
): State<LoadingResource<T>> {
    return produceState<LoadingResource<T>>(LoadingResource.Loading) {
        producer.invoke(this)
    }
}

@Composable
fun <T> produceLoadingResourceState(
    key: Any?,
    producer: suspend ProduceStateScope<LoadingResource<T>>.() -> Unit
): State<LoadingResource<T>> {
    return produceState<LoadingResource<T>>(LoadingResource.Loading, key) {
        value = LoadingResource.Loading // Without this, we don't reset to loading when key changes
        delay(50) // Give us a sec to settle (TODO h4x!)
        producer.invoke(this)
    }
}

fun LoadingResource<*>.isLoading() = this == LoadingResource.Loading
fun LoadingResource<*>.isLoaded() = this is LoadingResource.Value

fun <T> LoadingResource<T>.getOrNull(): T? = when (this) {
    LoadingResource.Loading -> null
    is LoadingResource.Value<T> -> value
}
