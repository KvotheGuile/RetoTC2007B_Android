package mx.fer.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import mx.fer.app.model.Post
import mx.fer.app.model.PostType
import mx.fer.app.model.TextSize
import mx.fer.app.ui.theme.FERMovilTheme
import mx.fer.app.views.components.PostCard
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

/**
 * Non-functional: large text for older users and people with low vision. Cases CP-37 to CP-39.
 */
@RunWith(AndroidJUnit4::class)
class AccessibilityTest {

    @get:Rule
    val compose = createComposeRule()

    private val post = Post(
        id = "p1",
        title = "Convocatoria de renovación de beca para el ciclo escolar 2027",
        description = "Revisa los requisitos y entrega tus documentos antes de la fecha límite.",
        linkImg = null,
        type = PostType.SCHOLARSHIP,
        publishedAt = Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000L)
    )

    /** Renders the theme on top of a phone whose own font size is [phoneScale]. */
    private fun fontScaleInsideTheme(phoneScale: Float, option: TextSize): Float {
        var measured = 0f
        compose.setContent {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, phoneScale)) {
                FERMovilTheme(darkTheme = false, textScale = option.factor) {
                    measured = LocalDensity.current.fontScale
                }
            }
        }
        compose.waitForIdle()
        return measured
    }

    @Test
    fun cp37_textOptionMultipliesThePhoneSize() {
        assertEquals(1.3f, fontScaleInsideTheme(1f, TextSize.EXTRA_LARGE), 0.01f)
    }

    @Test
    fun cp38_textIsCappedAt200PercentButNeverShrinksThePhoneSetting() {
        assertEquals(2f, fontScaleInsideTheme(1.8f, TextSize.EXTRA_LARGE), 0.01f)
    }

    @Test
    fun cp38b_phoneAlreadyAbove200PercentIsRespected() {
        assertEquals(2.2f, fontScaleInsideTheme(2.2f, TextSize.LARGE), 0.01f)
    }

    @Test
    fun cp39_postCardStaysReadableAt200Percent() {
        compose.setContent {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, 2f)) {
                FERMovilTheme(darkTheme = false) {
                    PostCard(post = post, onClick = {})
                }
            }
        }

        // Title, date and description all on screen; the date moves to its own line
        compose.onNodeWithText(post.title).assertIsDisplayed()
        compose.onNodeWithText("Hace 3 horas").assertIsDisplayed()
        compose.onNodeWithText(post.description).assertIsDisplayed()
    }
}
