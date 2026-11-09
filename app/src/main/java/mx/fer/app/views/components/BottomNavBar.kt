package mx.fer.app.views.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.fer.app.R

private val AlturaBarraNav = 76.dp
private val IconoBarraNav = 26.dp

enum class Destino(
    val etiqueta: String,
    @DrawableRes val iconoActivo: Int,
    @DrawableRes val iconoInactivo: Int
) {
    ANUNCIOS("Anuncios", R.drawable.ic_anuncios_filled, R.drawable.ic_anuncios_outline),
    INICIO("Inicio", R.drawable.ic_inicio_filled, R.drawable.ic_inicio_outline),
    PREFERENCIAS("Preferencias", R.drawable.ic_preferencias_filled, R.drawable.ic_preferencias_outline)
}

@Composable
fun BottomNavBar(
    seleccionado: Destino,
    onSeleccionar: (Destino) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AlturaBarraNav)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Destino.entries.forEach { destino ->
                NavItem(
                    destino = destino,
                    seleccionado = destino == seleccionado,
                    onClick = { onSeleccionar(destino) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    destino: Destino,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val color = if (seleccionado) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(
                if (seleccionado) destino.iconoActivo else destino.iconoInactivo
            ),
            contentDescription = destino.etiqueta,
            tint = color,
            modifier = Modifier.size(IconoBarraNav)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = destino.etiqueta,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}