package mx.fer.app.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FerTabs(
    opciones: List<String>,
    seleccionada: Int,
    onSeleccionar: (Int) -> Unit
) {
    Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth()) {
            opciones.forEachIndexed { index, etiqueta ->
                val activa = index == seleccionada
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSeleccionar(index) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = etiqueta,
                        modifier = Modifier.padding(vertical = 12.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activa) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .height(2.dp)
                            .background(
                                if (activa) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}