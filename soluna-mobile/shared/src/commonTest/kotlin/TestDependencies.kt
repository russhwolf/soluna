package com.russhwolf.soluna.mobile

import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass

expect fun <T> runBlocking(context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> T): T

expect fun createInMemorySqlDriver(): SqlDriver

expect annotation class RunWith(val value: KClass<out Runner>)
expect abstract class Runner
expect class AndroidJUnit4 : Runner
