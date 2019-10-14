@file:Suppress("unused")

package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.api.httpClientEngine
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.addlocation.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver

private val repository by lazy {
    SolunaRepository.Impl(
        database = createDatabase(NativeSqliteDriver(SolunaDb.Schema, "Soluna.db")),
        googleApiClient = GoogleApiClient.Impl(httpClientEngine)
    )
}

fun getLocationListViewModel() = LocationListViewModel(repository)
fun getAddLocationViewModel() = AddLocationViewModel(repository)
fun getLocationDetailViewModel(id: Long) = LocationDetailViewModel(id, repository)
