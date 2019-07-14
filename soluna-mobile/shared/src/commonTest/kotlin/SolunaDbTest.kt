package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SolunaDbTest {

    lateinit var driver: SqlDriver
    lateinit var database: SolunaDb

    @BeforeTest
    fun setup() {
        driver = createDriver()
        database = SolunaDb(driver, Reminder.Adapter(EnumColumnAdapter()))
    }

    @Test
    fun add_location() {
        val initialLocations = database.locationQueries.selectAll().executeAsList()
        assertTrue { initialLocations.isEmpty() }

        database.locationQueries.insert("test location", 42.3956001, -71.1387674, "America/New_York")

        val updatedLocations = database.locationQueries.selectAll().executeAsList()
        assertTrue { updatedLocations.size == 1 }
        assertTrue { updatedLocations[0].label == "test location" }
        assertTrue { updatedLocations[0].latitude == 42.3956001 }
        assertTrue { updatedLocations[0].longitude == -71.1387674 }
        assertTrue { updatedLocations[0].timeZone == "America/New_York" }
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
