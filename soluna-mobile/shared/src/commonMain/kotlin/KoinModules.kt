package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.GeocodeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initKoin(appModule: Module) = startKoin {
    modules(commonModule, platformModule, appModule)
}

internal val mainDispatcherQualifier = named("MainDispatcher")
internal val dbDispatcherQualifier = named("DbDispatcher")

internal val commonModule = module {
    single { createDatabase(get()) }
    single<GoogleApiClient> { GoogleApiClient.Impl(get()) }

    single<LocationRepository> { LocationRepository.Impl(get(), get(dbDispatcherQualifier)) }
    single<ReminderRepository> { ReminderRepository.Impl(get(), get(dbDispatcherQualifier)) }
    single<GeocodeRepository> { GeocodeRepository.Impl(get()) }

    factory { LocationListViewModel(get(), get(mainDispatcherQualifier)) }
    factory { AddLocationViewModel(get(), get(), get(mainDispatcherQualifier)) }
    factory { (id: Long) -> LocationDetailViewModel(id, get(), get(), get(mainDispatcherQualifier)) }
}

internal expect val platformModule: Module
