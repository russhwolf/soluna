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
import com.russhwolf.soluna.mobile.screen.BasePlatformViewModel
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel
import com.russhwolf.soluna.mobile.screen.reminderlist.ReminderListViewModel
import com.russhwolf.soluna.mobile.screen.settings.SettingsViewModel
import kotlinx.datetime.Clock
import org.koin.core.context.startKoin
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module

fun initKoin(appModule: Module) = startKoin {
    modules(commonModule, platformModule, appModule)
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
        viewModel { HomeViewModel(get(), get(), get(), get(mainDispatcherQualifier)) }
        viewModel { LocationListViewModel(get(), get(mainDispatcherQualifier)) }
        viewModel { AddLocationViewModel(get(), get(), get(), get(mainDispatcherQualifier)) }
        viewModel { params -> LocationDetailViewModel(params.get(), get(), get(), get(), get(mainDispatcherQualifier)) }
        viewModel { ReminderListViewModel(get(), get(mainDispatcherQualifier)) }
        viewModel { SettingsViewModel(get(mainDispatcherQualifier)) }
    }
}

internal expect val platformModule: Module

expect inline fun <reified T : BasePlatformViewModel> ScopeDSL.viewModel(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>
