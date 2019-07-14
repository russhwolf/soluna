package com.russhwolf.soluna.mobile

import com.squareup.sqldelight.db.SqlDriver
import kotlin.reflect.KClass

expect fun createDriver(): SqlDriver

expect annotation class RunWith(val value: KClass<out Runner>)
expect abstract class Runner
expect class AndroidJUnit4 : Runner
