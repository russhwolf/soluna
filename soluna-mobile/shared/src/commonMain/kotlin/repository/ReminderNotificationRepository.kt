package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

interface ReminderNotificationRepository {
    fun getUpcomingNotifications(): Flow<List<ReminderNotification>?>

    class Impl(
        private val locationRepository: LocationRepository,
        private val reminderRepository: ReminderRepository,
        private val astronomicalDataRepository: AstronomicalDataRepository,
        private val currentTimeRepository: CurrentTimeRepository
    ) : ReminderNotificationRepository {
        override fun getUpcomingNotifications(): Flow<List<ReminderNotification>?> {
            return combine(
                locationRepository.getSelectedLocation(),
                reminderRepository.getReminders(),
                ::getUpcomingNotificationsList
            )
        }

        private fun getUpcomingNotificationsList(
            location: SelectableLocation?,
            reminders: List<Reminder>
        ): List<ReminderNotification>? {
            location ?: return null
            val enabledReminders = reminders.filter { it.enabled }
            if (enabledReminders.isEmpty()) {
                return null
            }

            val timeZone = TimeZone.of(location.timeZone)
            val currentTime = currentTimeRepository.getCurrentTime()
            val todayAtLocation = currentTime.toLocalDateTime(timeZone).date

            return List(7) { i ->
                todayAtLocation.plus(i, DateTimeUnit.DAY)
            }.flatMap { localDate ->
                val times = astronomicalDataRepository.getTimes(
                    date = localDate,
                    zone = timeZone,
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                enabledReminders.mapNotNull { reminder ->
                    val eventTime = when (reminder.type) {
                        ReminderType.Sunrise -> times.sunriseTime
                        ReminderType.Sunset -> times.sunsetTime
                        ReminderType.Moonrise -> times.moonriseTime
                        ReminderType.Moonset -> times.moonsetTime
                    }
                    eventTime?.takeIf { it > currentTime }?.let {
                        ReminderNotification(
                            notificationTime = eventTime.minus(reminder.minutesBefore.minutes),
                            eventTime = eventTime,
                            type = reminder.type,
                            locationLabel = location.label,
                            timeZone = location.timeZone
                        )
                    }
                }
            }
        }
    }
}

data class ReminderNotification(
    val notificationTime: Instant,
    val eventTime: Instant,
    val type: ReminderType,
    val locationLabel: String,
    val timeZone: String
)
