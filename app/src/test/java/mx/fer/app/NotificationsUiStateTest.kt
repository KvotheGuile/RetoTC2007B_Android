package mx.fer.app

import mx.fer.app.model.Notification
import mx.fer.app.model.PostType
import mx.fer.app.viewmodels.NotificationsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/** Unread badge on the bell and read state per notice (RF-03). Cases CP-17 to CP-19. */
class NotificationsUiStateTest {

    private fun notif(id: String) = Notification(id, null, "t", "m", PostType.GENERAL, Date())

    @Test
    fun cp17_countsOnlyUnreadNotifications() {
        val state = NotificationsUiState(
            notifications = listOf(notif("a"), notif("b"), notif("c")),
            readIds = setOf("b")
        )
        assertEquals(2, state.unreadCount)
    }

    @Test
    fun cp18_readIdsOfDeletedNotificationsDoNotCount() {
        // A notice deleted in the panel can still be in readIds; it must not make the count negative
        val state = NotificationsUiState(
            notifications = listOf(notif("a")),
            readIds = setOf("a", "deleted")
        )
        assertEquals(0, state.unreadCount)
    }

    @Test
    fun cp19_isReadFollowsReadIds() {
        val state = NotificationsUiState(notifications = listOf(notif("a"), notif("b")), readIds = setOf("a"))
        assertTrue(state.isRead(notif("a")))
        assertFalse(state.isRead(notif("b")))
    }
}
