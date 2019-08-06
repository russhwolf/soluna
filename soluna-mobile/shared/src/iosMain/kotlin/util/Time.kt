package com.russhwolf.soluna.mobile.util

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.math.roundToLong

actual val epochSeconds: Long = NSDate().timeIntervalSince1970.roundToLong()
