package mx.fer.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Date.toRelativeTime(): String {
    val diff = System.currentTimeMillis() - time
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "Hace $minutes min"
        hours < 24 -> if (hours == 1L) "Hace 1 hora" else "Hace $hours horas"
        days < 30 -> if (days == 1L) "Hace 1 día" else "Hace $days días"
        else -> SimpleDateFormat("d MMM yyyy", Locale("es", "MX")).format(this)
    }
}

/** "Hace 3 días · 8 nov 2026". Past 30 days the relative part is already a date, so it shows once. */
fun Date.toFullDate(): String {
    val date = SimpleDateFormat("d MMM yyyy", Locale("es", "MX")).format(this)
    val relative = toRelativeTime()
    return if (relative == date) date else "$relative · $date"
}
