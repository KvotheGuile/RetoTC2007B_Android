package mx.fer.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mx.fer.app.model.PostType
import mx.fer.app.model.TextSize
import mx.fer.app.model.ThemeMode
import mx.fer.app.model.UserPreferencesRepository
import mx.fer.app.model.preferencesFromOnboarding
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RF-02: preferences live on the phone (DataStore) and survive reading them back.
 * Cases CP-28 to CP-32. Runs on a device/emulator and wipes this app's preferences there.
 */
@RunWith(AndroidJUnit4::class)
class UserPreferencesRepositoryTest {

    private val repo = UserPreferencesRepository(
        InstrumentationRegistry.getInstrumentation().targetContext
    )

    @Before
    fun setUp() = runBlocking { repo.clear() }

    @After
    fun tearDown() = runBlocking { repo.clear() }

    @Test
    fun cp28_beforeOnboardingThereAreNoPreferences() = runBlocking {
        assertNull(repo.preferences.first())   // null = the app shows onboarding
    }

    @Test
    fun cp29_onboardingResultIsSavedOnTheDevice() = runBlocking {
        repo.save(preferencesFromOnboarding(setOf(PostType.APPLICANTS), pushNotificationsEnabled = false))

        val saved = repo.preferences.first()!!
        assertTrue(saved.interestedApplicants)
        assertFalse(saved.interestedScholarship)
        assertFalse(saved.interestedGeneral)
        assertFalse(saved.pushNotificationsEnabled)
    }

    @Test
    fun cp30_changesFromPreferencesScreenAreKept() = runBlocking {
        repo.save(preferencesFromOnboarding(setOf(PostType.GENERAL), pushNotificationsEnabled = true))

        repo.setInterestedScholarship(true)
        repo.setPushNotificationsEnabled(false)
        repo.setThemeMode(ThemeMode.DARK)
        repo.setTextSize(TextSize.EXTRA_LARGE)
        repo.setReduceMotion(true)

        val saved = repo.preferences.first()!!
        assertTrue(saved.interestedScholarship)
        assertFalse(saved.pushNotificationsEnabled)
        assertEquals(ThemeMode.DARK, saved.themeMode)
        assertEquals(TextSize.EXTRA_LARGE, saved.textSize)
        assertEquals(true, saved.reduceMotion)
    }

    @Test
    fun cp31_readNotificationsAreRememberedAndCleanedUp() = runBlocking {
        repo.markNotificationsRead(listOf("n1", "n2"))
        assertEquals(setOf("n1", "n2"), repo.readNotificationIds.first())

        // n2 was deleted in the panel: it is forgotten so the set doesn't grow forever
        repo.keepReadNotificationsOnly(setOf("n1", "n3"))
        assertEquals(setOf("n1"), repo.readNotificationIds.first())
    }

    @Test
    fun cp32_clearBringsBackOnboarding() = runBlocking {
        repo.save(preferencesFromOnboarding(setOf(PostType.GENERAL), pushNotificationsEnabled = true))

        repo.clear()

        assertNull(repo.preferences.first())
    }
}
