package mx.fer.app.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PostItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    image: String? = null,
    date: String? = null,
    imageFirst: Boolean = false,
    descriptionMaxLines: Int = Int.MAX_VALUE
) {
    Column(modifier = modifier.fillMaxWidth()) {

        if (imageFirst && image != null) {
            PostImage(image, title)
            Spacer(Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (date != null) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = descriptionMaxLines,
            overflow = TextOverflow.Ellipsis
        )

        if (!imageFirst && image != null) {
            Spacer(Modifier.height(14.dp))
            PostImage(image, title)
        }
    }
}

/** Imagen desde URL con Coil. El fondo gris se ve mientras carga. */
@Composable
fun PostImage(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.outlineVariant),
        contentScale = ContentScale.Crop
    )
}