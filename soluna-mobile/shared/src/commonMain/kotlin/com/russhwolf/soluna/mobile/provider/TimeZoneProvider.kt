package com.russhwolf.soluna.mobile.provider

import kotlinx.datetime.TimeZone

interface TimeZoneProvider {
    fun getSystemTimeZone(): TimeZone

    fun getAvailableTimeZones(): Set<TimeZone>

    class Impl : TimeZoneProvider {
        override fun getSystemTimeZone(): TimeZone = TimeZone.currentSystemDefault()

        override fun getAvailableTimeZones(): Set<TimeZone> =
            TimeZone.availableZoneIds.map { TimeZone.of(it) }.toSet()
    }
}
