package mx.fer.app.views.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import mx.fer.app.R
import mx.fer.app.model.Post
import mx.fer.app.model.PostType
import mx.fer.app.util.toRelativeTime

/** Human label for each channel. */
val PostType.label: String
    get() = when (this) {
        PostType.GENERAL -> "Anuncio general"
        PostType.SCHOLARSHIP -> "Becarios"
        PostType.APPLICANTS -> "Aspirantes"
    }

@get:DrawableRes
val PostType.icon: Int
    get() = when (this) {
        PostType.GENERAL -> R.drawable.ic_general_announcement
        PostType.SCHOLARSHIP -> R.drawable.ic_school
        PostType.APPLICANTS -> R.drawable.ic_description
    }

/**
 * Feed post card (text first): title with the relative date on the right, the description
 * up to three lines, then the image below. No author or category: the section already says it.
 */
@Composable
fun PostCard(
    post: Post,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                role = Role.Button,
                onClickLabel = "Abrir publicación"
            ) { onClick(post.id) }
    ) {
        Column(Modifier.padding(16.dp)) {
            val date = post.publishedAt.toRelativeTime()
            if (LocalDensity.current.fontScale >= 1.25f) {
                // Large text: beside the title the date squeezes it until words break,
                // so it moves to its own line and the title gets the full width
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            } else {
                // Title and date share the first line; the date stays top-right
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 3.dp)   // aligns with the title's first line
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            post.linkImg?.let { url ->
                Spacer(Modifier.height(12.dp))
                PostImage(
                    url = url,
                    postId = post.id,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

/**
 * Large card for the newest post: full-bleed image with a dark gradient and the title in
 * white on top. Falls back to a navy card when the post has no image.
 */
@Composable
fun FeaturedPostCard(
    post: Post,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3.4f)
            .pressScale(interaction)
            .clip(MaterialTheme.shapes.large)
            .background(mx.fer.app.ui.theme.FerNavy)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                role = Role.Button,
                onClickLabel = "Abrir publicación"
            ) { onClick(post.id) }
    ) {
        post.linkImg?.let { url ->
            PostImage(url = url, postId = post.id, modifier = Modifier.fillMaxSize())
        }
        // Scrim so white text stays readable on any photo
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.35f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.78f)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = post.publishedAt.toRelativeTime(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Image from URL with Coil: fades in over a tinted placeholder, shared with the detail screen. */
@Composable
fun PostImage(
    url: String,
    postId: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .sharedPostImage(postId)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentScale = ContentScale.Crop
    )
}
