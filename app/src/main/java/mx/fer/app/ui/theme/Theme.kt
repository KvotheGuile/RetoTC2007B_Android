package mx.fer.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FerColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulPrincipal,
    background = FondoGeneral,
    onBackground = AzulOscuro,
    surface = Blanco,
    onSurface = AzulOscuro,
    onSurfaceVariant = GrisDescripcion,
    outlineVariant = DivisorSuave
)

@Composable
fun FerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FerColorScheme,
        typography = Typography,
        content = content
    )
}