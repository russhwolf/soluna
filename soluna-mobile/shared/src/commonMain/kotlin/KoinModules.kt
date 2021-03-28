package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.repository.AstronomicalDataRepository
import com.russhwolf.soluna.mobile.repository.ClockRepository
import com.russhwolf.soluna.mobile.repository.GeocodeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.repository.UpcomingTimesRepository
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import com.russhwolf.soluna.mobile.screen.reminderlist.ReminderListViewModel
import kotlinx.datetime.Clock
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initKoin(appModule: Module) = startKoin {
    modules(commonModule, platformModule, appModule)
}

internal val mainDispatcherQualifier = named("MainDispatcher")
internal val ioDispatcherQualifier = named("IoDispatcher")

internal val commonModule = module {
    single { createDatabase(get()) }
    single<GoogleApiClient> { GoogleApiClient.Impl(get()) }
    single<Clock> { Clock.System }

    single<LocationRepository> { LocationRepository.Impl(get(), get(), get(ioDispatcherQualifier)) }
    single<ReminderRepository> { ReminderRepository.Impl(get(), get(ioDispatcherQualifier)) }
    single<GeocodeRepository> { GeocodeRepository.Impl(get(), get()) }
    single<AstronomicalDataRepository> { AstronomicalDataRepository.Impl() }
    single<ClockRepository> { ClockRepository.Impl(get()) }
    single<UpcomingTimesRepository> { UpcomingTimesRepository.Impl(get(), get(), get()) }

    factory { HomeViewModel(get(), get(), get(), get(mainDispatcherQualifier)) }
    factory { LocationListViewModel(get(), get(mainDispatcherQualifier)) }
    factory { AddLocationViewModel(get(), get(), get(mainDispatcherQualifier)) }
    factory { (id: Long) -> LocationDetailViewModel(id, get(), get(mainDispatcherQualifier)) }
    factory { ReminderListViewModel(get(), get(mainDispatcherQualifier)) }
}

internal expect val platformModule: Module
