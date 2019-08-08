@file:Suppress("unused")

package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.api.httpClientEngine
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import kotlin.native.concurrent.ensureNeverFrozen

val repository by lazy {
    SolunaRepository.Impl(
        database = createDatabase(NativeSqliteDriver(SolunaDb.Schema, "Soluna.db")),
        googleApiClient = GoogleApiClient.Impl(httpClientEngine).also { it.ensureNeverFrozen() }
    )
}

