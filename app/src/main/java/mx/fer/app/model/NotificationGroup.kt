package mx.fer.app.model

import java.util.Calendar
import java.util.Date

/** Sections of the notification list, in display order. */
enum class NotificationGroup(val label: String) {
    TODAY("Hoy"),
    THIS_WEEK("Esta semana"),
    OLDER("Anteriores")
}

/**
 * Splits notifications into Hoy / Esta semana / Anteriores.
 * "Hoy" is the current calendar day; "Esta semana" is the 6 days before it.
 * Empty groups are left out and each group keeps newest first.
 */
fun List<Notification>.groupByRecency(now: Date = Date()): List<Pair<NotificationGroup, List<Notification>>> {
    val startOfToday = Calendar.getInstance().apply {
        time = now
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val todayMillis = startOfToday.timeInMillis
    val weekMillis = startOfToday.apply { add(Calendar.DAY_OF_YEAR, -6) }.timeInMillis

    return sortedByDescending { it.date }
        .groupBy { n ->
            when {
                n.date.time >= todayMillis -> NotificationGroup.TODAY
                n.date.time >= weekMillis -> NotificationGroup.THIS_WEEK
                else -> NotificationGroup.OLDER
            }
        }
        .toSortedMap()
        .toList()
}
