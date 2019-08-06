package com.russhwolf.soluna.mobile.util

import kotlin.math.roundToLong

actual val epochSeconds: Long = (System.currentTimeMillis() / 1000.0).roundToLong()
