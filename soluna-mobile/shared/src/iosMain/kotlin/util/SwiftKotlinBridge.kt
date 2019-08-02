@file:Suppress("unused")

package com.russhwolf.soluna.mobile.util

import com.russhwolf.soluna.mobile.SolunaRepository
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.russhwolf.soluna.mobile.db.createDatabase
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver

val repository by lazy { SolunaRepository.Impl(createDatabase(NativeSqliteDriver(SolunaDb.Schema, "Soluna.db"))) }

