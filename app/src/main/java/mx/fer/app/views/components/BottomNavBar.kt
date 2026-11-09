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
fun PostItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int? = null,
    date: String? = null,
    imageFirst: Boolean = false,
    descriptionMaxLines: Int = Int.MAX_VALUE
) {
    Column(modifier = modifier.fillMaxWidth()) {

        if (imageFirst) {
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

        if (!imageFirst) {
            Spacer(Modifier.height(14.dp))
            PostImage(image, title)
        }
    }
}

/**
 * Shows a gray placeholder when there is no image.
 * When real data arrives, swap Image for Coil's AsyncImage here.
 */
@Composable
fun PostImage(
    @DrawableRes image: Int?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    if (image != null) {
        Image(
            painter = painterResource(image),
            contentDescription = contentDescription,
            modifier = modifier
                .fillMaxWidth()
                .clip(shape),
            contentScale = ContentScale.FillWidth
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(shape)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}