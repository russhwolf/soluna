package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.db.Location
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.SelectAllLocations
import com.russhwolf.soluna.mobile.db.SolunaDb
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
    fun locationQueries_happyPath() {
        val initialLocations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(initialLocations.isEmpty())

        database.locationQueries.insertLocation(
            label = "Test Location",
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )

        val updatedLocations = database.locationQueries.selectAllLocations().executeAsList()
        assertEquals(1, updatedLocations.size)
        assertEquals(
            expected = SelectAllLocations.Impl(
                id = 1,
                label = "Test Location"
            ),
            actual = updatedLocations[0]
        )

        val location = database.locationQueries.selectLocationByLabel("Test Location").executeAsOne()
        assertEquals(
            expected = Location.Impl(
                id = 1,
                label = "Test Location",
                latitude = 42.3956001,
                longitude = -71.1387674,
                timeZone = "America/New_York"
            ),
            actual = location
        )

        database.locationQueries.updateLocationLabelById("Updated Location", location.id)

        val updatedLocation = database.locationQueries.selectLocationByLabel("Updated Location").executeAsOne()
        assertEquals(
            expected = Location.Impl(
                id = 1,
                label = "Updated Location",
                latitude = 42.3956001,
                longitude = -71.1387674,
                timeZone = "America/New_York"
            ),
            actual = updatedLocation
        )

        val oldLocation = database.locationQueries.selectLocationByLabel("Test Location").executeAsOneOrNull()
        assertNull(oldLocation)

        database.locationQueries.deleteLocationById(updatedLocation.id)
        val finalLocations = database.locationQueries.selectAllLocations().executeAsList()
        assertTrue(finalLocations.isEmpty())
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }
}
