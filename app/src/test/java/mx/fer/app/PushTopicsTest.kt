package mx.fer.app

import mx.fer.app.model.ALL_TOPICS
import mx.fer.app.model.UserPreferences
import mx.fer.app.model.topicsFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Push notifications only for the channels the user follows (RF-03). Cases CP-05 to CP-08. */
class PushTopicsTest {

    private fun prefs(
        push: Boolean = true,
        general: Boolean = false,
        scholars: Boolean = false,
        applicants: Boolean = false
    ) = UserPreferences(
        pushNotificationsEnabled = push,
        interestedScholarship = scholars,
        interestedApplicants = applicants,
        interestedGeneral = general
    )

    @Test
    fun cp05_subscribesOnlyToChosenChannels() {
        assertEquals(setOf("scholars"), topicsFor(prefs(scholars = true)))
        assertEquals(setOf("general", "applicants"), topicsFor(prefs(general = true, applicants = true)))
    }

    @Test
    fun cp06_allInterestsGiveAllTopics() {
        val topics = topicsFor(prefs(general = true, scholars = true, applicants = true))
        assertEquals(ALL_TOPICS.toSet(), topics)
    }

    @Test
    fun cp07_pushOffMeansNoTopicsEvenWithInterests() {
        val topics = topicsFor(prefs(push = false, general = true, scholars = true, applicants = true))
        assertTrue(topics.isEmpty())
    }

    @Test
    fun cp08_topicNamesMatchWhatThePanelSendsTo() {
        // The web panel publishes to these exact topic names
        assertEquals(listOf("general", "scholars", "applicants"), ALL_TOPICS)
    }
}
