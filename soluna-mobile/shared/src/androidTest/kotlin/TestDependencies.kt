package com.russhwolf.soluna.mobile

import androidx.test.core.app.ApplicationProvider
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual fun createDriver(): SqlDriver = AndroidSqliteDriver(SolunaDb.Schema, ApplicationProvider.getApplicationContext())

actual typealias RunWith = org.junit.runner.RunWith
actual typealias Runner = org.junit.runner.Runner
actual typealias AndroidJUnit4 = androidx.test.ext.junit.runners.AndroidJUnit4
