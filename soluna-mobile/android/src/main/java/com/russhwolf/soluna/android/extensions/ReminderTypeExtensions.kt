package com.russhwolf.soluna.android.extensions

import com.russhwolf.soluna.mobile.db.ReminderType

// TODO convert to string resource
val ReminderType.text
    get() = when (this) {
        ReminderType.Sunrise -> "sunrise"
        ReminderType.Sunset -> "sunset"
        ReminderType.Moonrise -> "moonrise"
        ReminderType.Moonset -> "moonset"
    }
