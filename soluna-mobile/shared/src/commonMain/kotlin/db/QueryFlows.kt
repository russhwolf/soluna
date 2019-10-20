package db

import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import com.russhwolf.soluna.mobile.util.runInMainThread
import com.squareup.sqldelight.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun <T : Any> Query<T>.asListFlow(): Flow<List<T>> = asFlow(Query<T>::executeAsList)
fun <T : Any> Query<T>.asOneOrNullFlow(): Flow<T?> = asFlow(Query<T>::executeAsOneOrNull)

private fun <T : Any, S> Query<T>.asFlow(
    execution: Query<T>.() -> S
): Flow<S> = callbackFlow {
    val listener: (S) -> Unit = { offer(it) }
    val listenerRef = ThreadLocalRef<(S) -> Unit>()
    listenerRef.value = listener
    val queryListener = object : Query.Listener {
        override fun queryResultsChanged() {
            val result = execution()
            runInMainThread({ result }) {
                listenerRef.get()?.invoke(it)
            }
        }
    }
    addListener(queryListener)
    awaitClose {
        removeListener(queryListener)
    }
}
