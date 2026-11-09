package mx.fer.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FerColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    background = FondoGeneral,
    onBackground = AzulOscuro,
    surface = Blanco,
    onSurface = AzulOscuro,
    onSurfaceVariant = GrisDescripcion,
    outlineVariant = GrisDescripcion.copy(alpha = 0.15f)
)

@Composable
fun FERMovilTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FerColorScheme,
        typography = Typography,
        shapes = FerShapes,
        content = content
    )
}