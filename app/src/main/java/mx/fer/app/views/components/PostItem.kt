package mx.fer.app.views.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun PublicacionItem(
    titulo: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    @DrawableRes imagen: Int? = null,
    fecha: String? = null,
    imagenPrimero: Boolean = false,
    maxLineasDescripcion: Int = Int.MAX_VALUE
) {
    Column(modifier = modifier.fillMaxWidth()) {

        if (imagenPrimero) {
            ImagenPublicacion(imagen, titulo)
            Spacer(Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (fecha != null) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = maxLineasDescripcion,
            overflow = TextOverflow.Ellipsis
        )

        if (!imagenPrimero) {
            Spacer(Modifier.height(14.dp))
            ImagenPublicacion(imagen, titulo)
        }
    }
}

@Composable
fun ImagenPublicacion(
    @DrawableRes imagen: Int?,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(16.dp)

    if (imagen != null) {
        Image(
            painter = painterResource(imagen),
            contentDescription = descripcion,
            modifier = modifier
                .fillMaxWidth()
                .clip(forma),
            contentScale = ContentScale.FillWidth
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(forma)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}