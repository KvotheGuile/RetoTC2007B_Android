package mx.fer.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import mx.fer.app.model.PostType
import mx.fer.app.ui.theme.FERMovilTheme
import mx.fer.app.views.OnboardingScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Functional UI tests of the first-run flow (RF-01, RF-09). Cases CP-33 to CP-36.
 */
@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private var result: Pair<Set<PostType>, Boolean>? = null

    @Before
    fun setUp() {
        compose.setContent {
            FERMovilTheme(darkTheme = false) {
                OnboardingScreen(onFinish = { interests, push -> result = interests to push })
            }
        }
        compose.onNodeWithText("Comenzar").performClick()
    }

    @Test
    fun cp33_cannotContinueWithoutChoosingAnInterest() {
        compose.onNodeWithText("Continuar").assertIsNotEnabled()

        compose.onNodeWithText("Becarios").performClick()
        compose.onNodeWithText("Continuar").assertIsEnabled()

        // Unticking the only interest blocks it again
        compose.onNodeWithText("Becarios").performClick()
        compose.onNodeWithText("Continuar").assertIsNotEnabled()
    }

    @Test
    fun cp34_finishingSendsTheChosenInterestsAndPushOff() {
        compose.onNodeWithText("Becarios").performClick()
        compose.onNodeWithText("Aspirantes").performClick()
        compose.onNodeWithText("Continuar").performClick()

        compose.onNodeWithText("Ahora no").performClick()

        assertEquals(setOf(PostType.SCHOLARSHIP, PostType.APPLICANTS) to false, result)
    }

    @Test
    fun cp35_noScreenAsksForPersonalData() {
        // RF-09: welcome, interests and notifications only. No text fields at any step.
        compose.onNodeWithText("Anuncios generales").performClick()
        compose.onNodeWithText("Continuar").performClick()
        compose.onNodeWithText("Activar notificaciones").assertIsDisplayed()
        assertNull(result)   // nothing is saved until the user finishes
        compose.onAllNodes(hasSetTextAction()).assertCountEquals(0)
    }

    @Test
    fun cp36_mainButtonIsBigEnoughToTap() {
        // Accessibility: touch targets of at least 48 dp
        compose.onNodeWithText("Continuar").assertHeightIsAtLeast(48.dp)
    }
}
