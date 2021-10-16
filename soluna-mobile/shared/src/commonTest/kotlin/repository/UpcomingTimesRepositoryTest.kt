package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UpcomingTimesRepositoryTest {
    private lateinit var now: Instant

    private val location = SelectableLocation(1, "Home", 27.18, 62.83, "UTC", false)

    private val astronomicalDataRepository = object : AstronomicalDataRepository {
        override fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double) =
            AstronomicalData(
                sunriseTime = date.atTime(6, date.dayOfMonth).toInstant(zone),
                sunsetTime = date.atTime(18, date.dayOfMonth).toInstant(zone),
                moonriseTime = date.atTime(20, date.dayOfMonth).toInstant(zone),
                moonsetTime = date.atTime(8, date.dayOfMonth).toInstant(zone)
            )
    }
    private val upcomingTimesRepository: UpcomingTimesRepository by lazy {
        UpcomingTimesRepository.Impl(astronomicalDataRepository, FakeCurrentTimeRepository(now))
    }

    @Test
    fun getUpcomingTimes_early() = suspendTest {
        now = LocalDate(2021, 1, 1).atTime(1, 0).toInstant(TimeZone.UTC)

        upcomingTimesRepository.getUpcomingTimes(location).test {
            assertEquals(
                UpcomingTimes(
                    sunriseTime = LocalDate(2021, 1, 1).atTime(6, 1).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 1).atTime(18, 1).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 1).atTime(20, 1).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 1).atTime(8, 1).toInstant(TimeZone.UTC)
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun getUpcomingTimes_midday() = suspendTest {
        now = LocalDate(2021, 1, 1).atTime(12, 0).toInstant(TimeZone.UTC)

        upcomingTimesRepository.getUpcomingTimes(location).test {
            assertEquals(
                UpcomingTimes(
                    sunriseTime = LocalDate(2021, 1, 2).atTime(6, 2).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 1).atTime(18, 1).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 1).atTime(20, 1).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 2).atTime(8, 2).toInstant(TimeZone.UTC)
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun getUpcomingTimes_tomorrowsTimes() = suspendTest {
        now = LocalDate(2021, 1, 1).atTime(23, 0).toInstant(TimeZone.UTC)

        upcomingTimesRepository.getUpcomingTimes(location).test {
            assertEquals(
                UpcomingTimes(
                    sunriseTime = LocalDate(2021, 1, 2).atTime(6, 2).toInstant(TimeZone.UTC),
                    sunsetTime = LocalDate(2021, 1, 2).atTime(18, 2).toInstant(TimeZone.UTC),
                    moonriseTime = LocalDate(2021, 1, 2).atTime(20, 2).toInstant(TimeZone.UTC),
                    moonsetTime = LocalDate(2021, 1, 2).atTime(8, 2).toInstant(TimeZone.UTC)
                ),
                awaitItem()
            )
        }
    }
}
