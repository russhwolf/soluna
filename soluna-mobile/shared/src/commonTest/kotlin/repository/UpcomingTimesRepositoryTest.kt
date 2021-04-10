package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.settings.MockSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpcomingTimesRepositoryTest {
    private lateinit var now: Instant

    private val driver = createInMemorySqlDriver()
    private val database = createDatabase(driver)
    private val settings = MockSettings().toFlowSettings(Dispatchers.Unconfined)
    private val locationRepository = LocationRepository.Impl(database, settings, Dispatchers.Unconfined)
    private val astronomicalDataRepository = object : AstronomicalDataRepository {
        override fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double) =
            AstronomicalData(
                sunriseTime = date.atTime(6, date.dayOfMonth).toInstant(zone),
                sunsetTime = date.atTime(18, date.dayOfMonth).toInstant(zone),
                moonriseTime = date.atTime(20, date.dayOfMonth).toInstant(zone),
                moonsetTime = date.atTime(8, date.dayOfMonth).toInstant(zone)
            )
    }
    private val clock = object : Clock {
        override fun now() = now
    }
    private val upcomingTimesRepository: UpcomingTimesRepository by lazy {
        UpcomingTimesRepository.Impl(locationRepository, astronomicalDataRepository, clock)
    }

    @Test
    fun getUpcomingTimes_whenLocationSelected() = suspendTest {
        now = LocalDate(2021, 1, 1).atTime(1, 0).toInstant(TimeZone.UTC)

        upcomingTimesRepository.getUpcomingTimes().test {
            assertEquals(null, expectItem())
            expectNoEvents()

            initializeSelectedLocation()

            assertEquals(
                UpcomingTimes(
                    sunriseTime = LocalDate(2021, 1, 1).atTime(6, 1).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 1).atTime(18, 1).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 1).atTime(20, 1).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 1).atTime(8, 1).toInstant(TimeZone.UTC)
                ),
                expectItem()
            )
        }
    }

    @Test
    fun getUpcomingTimes_midday() = suspendTest {
        now = LocalDate(2021, 1, 1).atTime(12, 0).toInstant(TimeZone.UTC)
        initializeSelectedLocation()

        upcomingTimesRepository.getUpcomingTimes().test {
            assertEquals(
                UpcomingTimes(
                    sunriseTime = LocalDate(2021, 1, 2).atTime(6, 2).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 1).atTime(18, 1).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 1).atTime(20, 1).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 2).atTime(8, 2).toInstant(TimeZone.UTC)
                ),
                expectItem()
            )
        }
    }

    @Test
    fun getUpcomingTimes_tomorrowsTimes() = suspendTest {
        now = LocalDate(2021, 1, 1).atTime(23, 0).toInstant(TimeZone.UTC)
        initializeSelectedLocation()

        upcomingTimesRepository.getUpcomingTimes().test {
            assertEquals(
                UpcomingTimes(
                    sunriseTime = LocalDate(2021, 1, 2).atTime(6, 2).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 2).atTime(18, 2).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 2).atTime(20, 2).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 2).atTime(8, 2).toInstant(TimeZone.UTC)
                ),
                expectItem()
            )
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    private suspend fun initializeSelectedLocation() {
        locationRepository.addLocation("Home", 27.18, 62.83, "UTC")
        locationRepository.toggleSelectedLocation(1)
    }
}
