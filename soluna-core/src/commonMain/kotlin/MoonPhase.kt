package com.russhwolf.soluna

import com.russhwolf.soluna.math.Degree
import com.russhwolf.soluna.math.deg

enum class MoonPhase(internal val angle: Degree) { NEW(0.deg), FIRST_QUARTER(90.deg), FULL(180.deg), LAST_QUARTER(270.deg) }
