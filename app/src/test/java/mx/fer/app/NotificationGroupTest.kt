package mx.fer.app

import mx.fer.app.model.Notification
import mx.fer.app.model.NotificationGroup
import mx.fer.app.model.PostType
import mx.fer.app.model.groupByRecency
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

class NotificationGroupTest {

    // Fixed "now": 13 nov 2026, 10:00
    private val now: Date = Calendar.getInstance().apply {
        set(2026, Calendar.NOVEMBER, 13, 10, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    private fun at(daysAgo: Int, hour: Int) = Calendar.getInstance().apply {
        time = now
        add(Calendar.DAY_OF_YEAR, -daysAgo)
        set(Calendar.HOUR_OF_DAY, hour)
    }.time

    private fun notif(id: String, date: Date) =
        Notification(id, null, "t", "m", PostType.GENERAL, date)

    @Test
    fun splitsIntoTodayThisWeekAndOlder() {
        val list = listOf(
            notif("old", at(7, 23)),      // 8 days back counting today -> Anteriores
            notif("week6", at(6, 0)),     // start of the 7-day window -> Esta semana
            notif("yesterday", at(1, 23)),
            notif("today", at(0, 0))      // midnight today -> Hoy
        )

        val groups = list.groupByRecency(now)

        assertEquals(
            listOf(NotificationGroup.TODAY, NotificationGroup.THIS_WEEK, NotificationGroup.OLDER),
            groups.map { it.first }
        )
        assertEquals(listOf("today"), groups[0].second.map { it.id })
        assertEquals(listOf("yesterday", "week6"), groups[1].second.map { it.id })
        assertEquals(listOf("old"), groups[2].second.map { it.id })
    }

    @Test
    fun leavesOutEmptyGroups() {
        val groups = listOf(notif("a", at(20, 9))).groupByRecency(now)
        assertEquals(listOf(NotificationGroup.OLDER), groups.map { it.first })
    }
}
