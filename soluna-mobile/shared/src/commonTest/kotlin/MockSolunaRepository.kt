package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.LocationSummary
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.db.ReminderWithLocation

class MockSolunaRepository(
    locations: List<Location> = mutableListOf(),
    reminders: List<Reminder> = mutableListOf(),
    geocodeMap: Map<String, GeocodeData> = mutableMapOf()
) : SolunaRepository {
    private var nextLocationId = 0L
    private var nextReminderId = 0L
    private val locations = locations.toMutableList()
    private val reminders = reminders.toMutableList()
    private val geocodeMap = geocodeMap.toMutableMap()

    override suspend fun getLocations(): List<LocationSummary> = locations.map { LocationSummary.Impl(it.id, it.label) }

    override suspend fun getLocation(id: Long): Location? = locations.find { it.id == id }

    override suspend fun addLocation(label: String, latitude: Double, longitude: Double, timeZone: String) {
        locations.add(Location.Impl(nextLocationId++, label, latitude, longitude, timeZone))
    }

    override suspend fun deleteLocation(id: Long) {
        locations.removeAll { it.id == id }
    }

    override suspend fun updateLocationLabel(id: Long, label: String) {
        val index = locations.indexOfFirst { it.id == id }
        if (index < 0) return

        val prevLocation = locations[index]
        locations[index] =
            Location.Impl(id, label, prevLocation.latitude, prevLocation.longitude, prevLocation.timeZone)
    }

    override suspend fun getReminders(locationId: Long?): List<ReminderWithLocation> = reminders.map { reminder ->
        ReminderWithLocation.Impl(
            reminder.id,
            reminder.locationId,
            locations.first { it.id == reminder.locationId }.label,
            reminder.type,
            reminder.minutesBefore,
            reminder.enabled
        )
    }.run {
        if (locationId != null) {
            filter { it.id == locationId }
        } else {
            this
        }
    }

    override suspend fun addReminder(locationId: Long, type: ReminderType, minutesBefore: Int, enabled: Boolean) {
        reminders.add(Reminder.Impl(nextReminderId++, locationId, type, minutesBefore, enabled))
    }

    override suspend fun deleteReminder(id: Long) {
        reminders.removeAll { it.id == id }
    }

    override suspend fun updateReminder(id: Long, minutesBefore: Int?, enabled: Boolean?) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index < 0) return

        val prevReminder = reminders[index]
        reminders[index] =
            Reminder.Impl(
                id,
                prevReminder.locationId,
                prevReminder.type,
                minutesBefore ?: prevReminder.minutesBefore,
                enabled ?: prevReminder.enabled
            )
    }

    override suspend fun geocodeLocation(location: String): GeocodeData? = geocodeMap[location]
}
