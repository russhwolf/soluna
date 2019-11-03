package db

import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import com.russhwolf.soluna.mobile.util.isMainThread
import com.russhwolf.soluna.mobile.util.runInBackground
import com.russhwolf.soluna.mobile.util.runInMainThread
import com.squareup.sqldelight.Query
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

fun <T : Any> Query<T>.asListFlow(): Flow<List<T>> = asExecutionFlow(Query<T>::executeAsList)
fun <T : Any> Query<T>.asOneOrNullFlow(): Flow<T?> = asExecutionFlow(Query<T>::executeAsOneOrNull)

private inline fun <T : Any, S> Query<T>.asExecutionFlow(crossinline execution: Query<T>.() -> S): Flow<S> =
    asUnitFlow().map { runInBackground { execution() } }.distinctUntilChanged()

private fun <T : Any> Query<T>.asUnitFlow(): Flow<Unit> = callbackFlow {
    if (!isMainThread) {
        error("Query flow must be created from main thread!")
    }

    val ref = ThreadLocalRef<ProducerScope<Unit>>().also { it.value = this }
    val queryListener = object : Query.Listener {
        override fun queryResultsChanged() {
            if (isMainThread) {
                ref.value!!.offer(Unit)
            } else {
                runInMainThread {
                    ref.value!!.offer(Unit)
                }
            }
        }
    }
    addListener(queryListener)
    awaitClose {
        removeListener(queryListener)
    }
}
