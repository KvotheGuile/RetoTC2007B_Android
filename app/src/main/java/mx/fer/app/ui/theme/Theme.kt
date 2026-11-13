package mx.fer.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import mx.fer.app.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightPrimary,
    onSecondary = LightOnPrimary,
    secondaryContainer = LightPrimaryContainer,
    onSecondaryContainer = LightOnPrimaryContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceContainer,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = LightSurface,
    surfaceContainerLow = LightSurface,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHigh,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = ErrorLight
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkPrimary,
    onSecondary = DarkOnPrimary,
    secondaryContainer = DarkPrimaryContainer,
    onSecondaryContainer = DarkOnPrimaryContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceContainer,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHigh,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = ErrorDark
)

/** Resolves the user's "Apariencia" setting to light or dark. */
@Composable
fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

/** The text-size option can't take the total past this (unless the phone alone is already higher). */
private const val MaxFontScale = 2f

/**
 * [textScale] is the in-app "Tamaño del texto" option. It multiplies the phone's own font size,
 * so someone who already uses large text on their phone never sees the app smaller.
 */
@Composable
fun FERMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    textScale: Float = 1f,
    content: @Composable () -> Unit
) {
    // Always wrap in the same provider, even at 1x: switching between two code paths would
    // rebuild the whole app and reset the open tab when the user changes the size.
    val system = LocalDensity.current
    val density = if (textScale == 1f) {
        system   // untouched, keeps Android 14's non-linear font scaling
    } else {
        val fontScale = (system.fontScale * textScale)
            .coerceAtMost(maxOf(system.fontScale, MaxFontScale))
        Density(system.density, fontScale)
    }
    CompositionLocalProvider(LocalDensity provides density) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = Typography,
            shapes = FerShapes,
            content = content
        )
    }
}

/**
 * Caps the font scale for small fixed-width chrome (nav bar labels, the bell badge) where text
 * can't wrap. Body text is never capped this way: it reflows and should grow freely.
 */
@Composable
fun CappedFontScale(max: Float, content: @Composable () -> Unit) {
    val current = LocalDensity.current
    if (current.fontScale <= max) return content()
    CompositionLocalProvider(
        LocalDensity provides Density(current.density, max),
        content = content
    )
}
