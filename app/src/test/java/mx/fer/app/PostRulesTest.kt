package mx.fer.app

import mx.fer.app.model.PostType
import mx.fer.app.model.isActive
import mx.fer.app.model.toFirestore
import mx.fer.app.model.toPostType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/**
 * Channels (RF-04 to RF-06) and hiding expired posts (RF-07). Cases CP-09 to CP-12.
 */
class PostRulesTest {

    private val now = Date(1_800_000_000_000L)

    @Test
    fun cp09_eachChannelSurvivesTheTripToFirestoreAndBack() {
        for (type in PostType.entries) {
            assertEquals(type, type.toFirestore().toPostType())
        }
    }

    @Test
    fun cp10_unknownOrMissingChannelFallsBackToGeneral() {
        assertEquals(PostType.GENERAL, null.toPostType())
        assertEquals(PostType.GENERAL, "".toPostType())
        assertEquals(PostType.GENERAL, "algo-nuevo".toPostType())
    }

    @Test
    fun cp11_postWithoutExpiryIsAlwaysShown() {
        assertTrue(isActive(expiresAt = null, now = now))
    }

    @Test
    fun cp12_expiredPostIsHiddenAndFutureOneIsShown() {
        assertFalse(isActive(Date(now.time - 1), now))
        assertFalse(isActive(now, now))   // expires exactly now: already gone
        assertTrue(isActive(Date(now.time + 60_000), now))
    }
}
