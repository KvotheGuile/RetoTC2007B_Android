package mx.fer.app

import mx.fer.app.util.toFullDate
import mx.fer.app.util.toRelativeTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/** Date shown on each post and in the detail screen (RF-07, RF-08). Cases CP-13 to CP-16. */
class RelativeTimeTest {

    private val now = 1_800_000_000_000L
    private val minute = 60_000L
    private val hour = 60 * minute
    private val day = 24 * hour

    private fun ago(ms: Long) = Date(now - ms)

    @Test
    fun cp13_minutesAndHours() {
        assertEquals("Ahora", ago(30_000).toRelativeTime(now))
        assertEquals("Hace 5 min", ago(5 * minute).toRelativeTime(now))
        assertEquals("Hace 1 hora", ago(hour).toRelativeTime(now))
        assertEquals("Hace 3 horas", ago(3 * hour).toRelativeTime(now))
    }

    @Test
    fun cp14_daysUseSingularAndPlural() {
        assertEquals("Hace 1 día", ago(day).toRelativeTime(now))
        assertEquals("Hace 29 días", ago(29 * day).toRelativeTime(now))
    }

    @Test
    fun cp15_after30DaysShowsTheDateInstead() {
        val text = ago(40 * day).toRelativeTime(now)
        assertFalse(text.startsWith("Hace"))
        assertTrue(text.matches(Regex("""\d{1,2} \S+ \d{4}""")))
    }

    @Test
    fun cp16_fullDateDoesNotRepeatTheDate() {
        val recent = ago(3 * day).toFullDate(now)
        assertTrue(recent.startsWith("Hace 3 días · "))

        val old = ago(40 * day)
        assertEquals(old.toRelativeTime(now), old.toFullDate(now))
    }
}
