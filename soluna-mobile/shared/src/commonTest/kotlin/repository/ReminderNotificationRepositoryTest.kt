package com.russhwolf.soluna.mobile.repository

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.soluna.mobile.createInMemorySqlDriver
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.createDatabase
import com.russhwolf.soluna.mobile.suspendTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReminderNotificationRepositoryTest {
    private val now = LocalDateTime(2021, 1, 1, 8, 0).toInstant(TimeZone.UTC)

    private val driver = createInMemorySqlDriver()
    private val database = createDatabase(driver)
    private val settings = MapSettings().toFlowSettings(Dispatchers.Unconfined)
    private val locationRepository = LocationRepository.Impl(database, settings, Dispatchers.Unconfined)
    private val reminderRepository = ReminderRepository.Impl(database, Dispatchers.Unconfined)
    private val astronomicalDataRepository = object : AstronomicalDataRepository {
        override fun getTimes(date: LocalDate, zone: TimeZone, latitude: Double, longitude: Double) =
            AstronomicalData(
                sunriseTime = date.atTime(6, date.dayOfMonth).toInstant(zone),
                sunsetTime = date.atTime(18, date.dayOfMonth).toInstant(zone),
                moonriseTime = date.atTime(20, date.dayOfMonth).toInstant(zone),
                moonsetTime = date.atTime(8, date.dayOfMonth).toInstant(zone)
            )
    }
    private val currentTimeRepository = FakeCurrentTimeRepository(now)

    private val repository by lazy {
        ReminderNotificationRepository.Impl(
            locationRepository,
            reminderRepository,
            astronomicalDataRepository,
            currentTimeRepository
        )
    }

    @Test
    fun upcomingNotifications_nullIfNoLocation() = suspendTest {
        reminderRepository.addReminder(ReminderType.Sunset, 15)

        repository.getUpcomingNotifications().test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun upcomingNotifications_nullIfNoReminders() = suspendTest {
        locationRepository.addLocation("Home", 0.0, 0.0, "UTC")
        locationRepository.toggleSelectedLocation(locationRepository.getLocations().first().first().id)

        repository.getUpcomingNotifications().test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun upcomingNotifications_returnsEnabledReminders() = suspendTest {
        locationRepository.addLocation("Here", 0.0, 0.0, "UTC")
        locationRepository.toggleSelectedLocation(locationRepository.getLocations().first().first().id)
        reminderRepository.addReminder(ReminderType.Sunset, 15, enabled = false)
        reminderRepository.addReminder(ReminderType.Sunrise, 15)

        repository.getUpcomingNotifications().test {
            assertEquals(
                expected = List(6) { i ->
                    ReminderNotification(
                        notificationTime = LocalDateTime(2021, 1, 2 + i, 5, 45 + 2 + i).toInstant(TimeZone.UTC),
                        eventTime = LocalDateTime(2021, 1, 2 + i, 6, 2 + i).toInstant(TimeZone.UTC),
                        type = ReminderType.Sunrise,
                        locationLabel = "Here",
                        timeZone = "UTC"
                    )
                },
                actual = awaitItem()
            )
        }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
