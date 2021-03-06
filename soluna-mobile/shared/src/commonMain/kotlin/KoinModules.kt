package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.provider.TimeZoneProvider
import com.russhwolf.soluna.mobile.repository.AstronomicalDataRepository
import com.russhwolf.soluna.mobile.repository.CurrentTimeRepository
import com.russhwolf.soluna.mobile.repository.GeocodeRepository
import com.russhwolf.soluna.mobile.repository.LocationRepository
import com.russhwolf.soluna.mobile.repository.ReminderNotificationRepository
import com.russhwolf.soluna.mobile.repository.ReminderRepository
import com.russhwolf.soluna.mobile.repository.UpcomingTimesRepository
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import com.russhwolf.soluna.mobile.screen.reminderlist.ReminderListViewModel
import kotlinx.datetime.Clock
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initKoin(appModule: Module, additionalConfig: KoinApplication.() -> Unit = {}) = startKoin {
    modules(commonModule, platformModule, appModule)
    additionalConfig()
}

internal val mainDispatcherQualifier = named("MainDispatcher")
internal val ioDispatcherQualifier = named("IoDispatcher")
val koinUiScopeQualifier = named("uiScope")

internal val commonModule = module {
    single { createDatabase(get()) }
    single<GoogleApiClient> { GoogleApiClient.Impl(get(), get()) }
    single<Clock> { Clock.System }
    single<TimeZoneProvider> { TimeZoneProvider.Impl() }

    single<LocationRepository> { LocationRepository.Impl(get(), get(), get(ioDispatcherQualifier)) }
    single<ReminderRepository> { ReminderRepository.Impl(get(), get(ioDispatcherQualifier)) }
    single<GeocodeRepository> { GeocodeRepository.Impl(get(), get()) }
    single<AstronomicalDataRepository> { AstronomicalDataRepository.Impl() }
    single<CurrentTimeRepository> { CurrentTimeRepository.Impl(get()) }
    single<UpcomingTimesRepository> { UpcomingTimesRepository.Impl(get(), get()) }
    single<ReminderNotificationRepository> { ReminderNotificationRepository.Impl(get(), get(), get(), get()) }

    scope(koinUiScopeQualifier) {
        factory { HomeViewModel(get(), get(), get(), get(mainDispatcherQualifier)) }
        factory { LocationListViewModel(get(), get(mainDispatcherQualifier)) }
        factory { AddLocationViewModel(get(), get(), get(), get(mainDispatcherQualifier)) }
        factory { params -> LocationDetailViewModel(params.get(), get(), get(), get(mainDispatcherQualifier)) }
        factory { ReminderListViewModel(get(), get(mainDispatcherQualifier)) }
    }
}

internal expect val platformModule: Module
