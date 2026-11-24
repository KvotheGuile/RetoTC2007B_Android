package mx.fer.app

import mx.fer.app.model.PostType
import mx.fer.app.model.TextSize
import mx.fer.app.model.ThemeMode
import mx.fer.app.model.UserPreferences
import mx.fer.app.model.preferencesFromOnboarding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** RF-01 (onboarding with interests) and RF-09 (no personal data). Cases CP-01 to CP-04. */
class OnboardingPreferencesTest {

    @Test
    fun cp01_onlyTheChosenInterestsAreTurnedOn() {
        val prefs = preferencesFromOnboarding(setOf(PostType.SCHOLARSHIP), pushNotificationsEnabled = true)

        assertTrue(prefs.interestedScholarship)
        assertFalse(prefs.interestedApplicants)
        assertFalse(prefs.interestedGeneral)
    }

    @Test
    fun cp02_allThreeInterestsCanBeChosen() {
        val prefs = preferencesFromOnboarding(PostType.entries.toSet(), pushNotificationsEnabled = true)

        assertTrue(prefs.interestedScholarship && prefs.interestedApplicants && prefs.interestedGeneral)
    }

    @Test
    fun cp03_pushChoiceFromOnboardingIsKept() {
        val off = preferencesFromOnboarding(setOf(PostType.GENERAL), pushNotificationsEnabled = false)
        val on = preferencesFromOnboarding(setOf(PostType.GENERAL), pushNotificationsEnabled = true)

        assertFalse(off.pushNotificationsEnabled)
        assertTrue(on.pushNotificationsEnabled)
    }

    @Test
    fun cp03b_accessibilityOptionsStartFollowingThePhone() {
        val prefs = preferencesFromOnboarding(setOf(PostType.GENERAL), pushNotificationsEnabled = true)

        assertEquals(ThemeMode.SYSTEM, prefs.themeMode)
        assertEquals(TextSize.SYSTEM, prefs.textSize)
        assertNull(prefs.reduceMotion)   // null = follow Android's "Remove animations"
    }

    @Test
    fun cp04_preferencesHoldNoPersonalData() {
        // RF-09: the stored model only has these fields. If someone adds a name, email,
        // phone or similar field, this test fails and forces a privacy review.
        val fields = UserPreferences::class.java.declaredFields
            .map { it.name }
            .filterNot { it.startsWith("$") }
            .toSet()

        assertEquals(
            setOf(
                "pushNotificationsEnabled", "interestedScholarship", "interestedApplicants",
                "interestedGeneral", "themeMode", "textSize", "reduceMotion"
            ),
            fields
        )
    }
}
