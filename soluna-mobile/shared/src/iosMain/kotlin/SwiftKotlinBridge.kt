@file:Suppress("unused")

package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.repository.GeocodeRepository
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import io.ktor.client.engine.ios.Ios


private val database by lazy { createDatabase(NativeSqliteDriver(SolunaDb.Schema, "Soluna.db")) }
private val googleApiClient by lazy { GoogleApiClient.Impl(Ios.create()) }

private val locationRepository by lazy { LocationRepository.Impl(database) }
private val reminderRepository by lazy { ReminderRepository.Impl(database) }
private val geocodeRepository by lazy { GeocodeRepository.Impl(googleApiClient) }

fun getLocationListViewModel() = LocationListViewModel(locationRepository)
fun getAddLocationViewModel() = AddLocationViewModel(locationRepository, geocodeRepository)
fun getLocationDetailViewModel(id: Long) = LocationDetailViewModel(id, locationRepository, reminderRepository)
