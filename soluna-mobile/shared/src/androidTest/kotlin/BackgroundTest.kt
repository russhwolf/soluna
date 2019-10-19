package com.russhwolf.soluna.mobile.util

import android.os.Looper
import org.robolectric.Shadows.shadowOf

actual fun blockUntilIdle() = shadowOf(Looper.getMainLooper()).idle()
