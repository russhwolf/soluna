package com.russhwolf.soluna.mobile.db

enum class ReminderType {
    Sunrise, Sunset, Moonrise, Moonset;

    companion object {
        // Redeclare this to make it visible to Swift
        val values = values().toList()
    }
}
