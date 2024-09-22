import com.russhwolf.soluna.AstronomicalCalculator
import com.russhwolf.soluna.MoonPhase
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.calendar.CalendarCellData
import com.russhwolf.soluna.calendar.CalendarDataGenerator
import com.russhwolf.soluna.calendar.CalendarDataHelper
import com.russhwolf.soluna.calendar.DstEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class CalendarModelTest {
    @Test
    fun calendarDataGenerator() {
        val generator = CalendarDataGenerator<Long, TestMonth, TestDayOfWeek>(TestCalendarDataHelper()) { _, _, day ->
            TestAstronomicalCalculator(day)
        }

        val calendarMonth =
            generator.generateCalendarMonth(2024, TestMonth.May, TestDayOfWeek.Sun, 3.14, 6.28, TestTimeZone("UTC"))

        val weeks = calendarMonth.weeks
        assertEquals(5, weeks.size)

        for (week in weeks) {
            assertEquals(7, week.days.size)
        }

        // Assert first and last weeks because that's probably enough redundancy and can hit all cell cases

        assertEquals(CalendarCellData.Empty, weeks[0][0])
        assertEquals(CalendarCellData.Empty, weeks[0][1])
        assertEquals(
            CalendarCellData.Data(
                1,
                RiseSetResult.RiseThenSet(1001L, 2001L),
                RiseSetResult.RiseThenSet(3001L, 4001L),
                null,
                null
            ), weeks[0][2]
        )
        assertEquals(
            CalendarCellData.Data(
                2,
                RiseSetResult.RiseThenSet(1002L, 2002L),
                RiseSetResult.RiseThenSet(3002L, 4002L),
                null,
                null
            ), weeks[0][3]
        )
        assertEquals(
            CalendarCellData.Data(
                3,
                RiseSetResult.RiseThenSet(1003L, 2003L),
                RiseSetResult.RiseThenSet(3003L, 4003L),
                null,
                DstEvent.End
            ), weeks[0][4]
        )
        assertEquals(
            CalendarCellData.Data(
                4,
                RiseSetResult.RiseThenSet(1004L, 2004L),
                RiseSetResult.RiseThenSet(3004L, 4004L),
                null,
                null
            ), weeks[0][5]
        )
        assertEquals(
            CalendarCellData.Data(
                5,
                RiseSetResult.RiseThenSet(1005L, 2005L),
                RiseSetResult.RiseThenSet(3005L, 4005L),
                null,
                null
            ), weeks[0][6]
        )

        assertEquals(
            CalendarCellData.Data(
                27,
                RiseSetResult.RiseThenSet(1027L, 2027L),
                RiseSetResult.RiseThenSet(3027L, 4027L),
                null,
                null
            ), weeks[4][0]
        )
        assertEquals(
            CalendarCellData.Data(
                28,
                RiseSetResult.RiseThenSet(1028L, 2028L),
                RiseSetResult.RiseThenSet(3028L, 4028L),
                MoonPhase.LAST_QUARTER,
                null
            ), weeks[4][1]
        )
        assertEquals(
            CalendarCellData.Data(
                29,
                RiseSetResult.RiseThenSet(1029L, 2029L),
                RiseSetResult.RiseThenSet(3029L, 4029L),
                null,
                null
            ), weeks[4][2]
        )
        assertEquals(
            CalendarCellData.Data(
                30,
                RiseSetResult.RiseThenSet(1030L, 2030L),
                RiseSetResult.RiseThenSet(3030L, 4030L),
                null,
                null
            ), weeks[4][3]
        )
        assertEquals(CalendarCellData.Empty, weeks[4][4])
        assertEquals(CalendarCellData.Empty, weeks[4][5])
        assertEquals(CalendarCellData.Empty, weeks[4][6])
    }
}

// These helpers are not realistic but they're enough to test CalendarDataGenerator

class TestAstronomicalCalculator(val day: Int) : AstronomicalCalculator<Long> {
    override val sunTimes: RiseSetResult<Long> = RiseSetResult.RiseThenSet(1000 + day.toLong(), 2000 + day.toLong())
    override val moonTimes: RiseSetResult<Long> = RiseSetResult.RiseThenSet(3000 + day.toLong(), 4000 + day.toLong())
    override val moonPhase: MoonPhase? = if (day % 7 == 0) MoonPhase.entries[day / 7 - 1] else null
}

class TestCalendarDataHelper : CalendarDataHelper<TestMonth, TestDayOfWeek> {
    var dstDate: Int? = -3
    var weekdayOfMonthStart = TestDayOfWeek.Tue

    override fun getDaysInMonth(year: Int, month: TestMonth): Int = 30

    override fun getDstEvent(year: Int, month: TestMonth, day: Int): DstEvent? = when (day) {
        dstDate -> DstEvent.Start
        dstDate?.unaryMinus() -> DstEvent.End
        else -> null
    }

    override fun getWeekdayOfMonthStart(year: Int, month: TestMonth): TestDayOfWeek = weekdayOfMonthStart

    override fun getIndexOfDayOfWeek(dayOfWeek: TestDayOfWeek): Int = dayOfWeek.ordinal + 1

}

enum class TestMonth {
    Jan, Feb, Mar, Apr, May, Jun, Jul, Sep, Oct, Nov, Dec
}

enum class TestDayOfWeek {
    Mon, Tue, Wed, Thu, Fri, Sat, Sun
}

data class TestTimeZone(val id: String)
