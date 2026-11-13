package mx.fer.app.views.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mx.fer.app.ui.theme.Dimens

/** Soft moving highlight used by the loading placeholders. */
@Composable
fun Modifier.shimmer(shape: Shape = MaterialTheme.shapes.small): Modifier {
    val base = MaterialTheme.colorScheme.surfaceContainerHigh
    val highlight = MaterialTheme.colorScheme.surface
    // Static placeholder instead of a sweeping highlight
    if (LocalReduceMotion.current) return clip(shape).background(base)
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerX"
    )
    return clip(shape).background(
        Brush.linearGradient(
            colors = listOf(base, highlight.copy(alpha = 0.6f), base),
            start = androidx.compose.ui.geometry.Offset(x, 0f),
            end = androidx.compose.ui.geometry.Offset(x + 400f, 200f)
        )
    )
}

@Composable
private fun Bone(height: Dp, widthFraction: Float = 1f) {
    Box(
        Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .shimmer()
    )
}

/** Placeholder with the same shape as a PostCard. */
@Composable
fun PostCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Title + date on one line
            androidx.compose.foundation.layout.Row {
                Box(Modifier.weight(1f)) { Bone(18.dp, 0.8f) }
                Spacer(Modifier.width(24.dp))
                Box(Modifier.width(56.dp)) { Bone(12.dp) }
            }
            Bone(14.dp)
            Bone(14.dp)
            Bone(14.dp, 0.6f)
            Spacer(Modifier.height(2.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .shimmer(MaterialTheme.shapes.medium)
            )
        }
    }
}

/** Placeholder with the same shape as a NotificationCard: round icon, title + date, two lines. */
@Composable
fun NotificationCardSkeleton(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Box(
            Modifier
                .size(40.dp)
                .shimmer(androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            androidx.compose.foundation.layout.Row {
                Box(Modifier.weight(1f)) { Bone(16.dp, 0.7f) }
                Spacer(Modifier.width(24.dp))
                Box(Modifier.width(48.dp)) { Bone(12.dp) }
            }
            Bone(13.dp)
            Bone(13.dp, 0.55f)
        }
    }
}

/** List of notification placeholders, shown while notifications load. */
@Composable
fun NotificationListSkeleton(modifier: Modifier = Modifier, count: Int = 6) {
    Column(
        modifier
            .fillMaxSize()
            .semantics { contentDescription = "Cargando notificaciones" }
            .padding(horizontal = Dimens.ScreenPadding, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(count) { NotificationCardSkeleton() }
    }
}

/** Full-screen list of placeholders, shown while the feed loads. */
@Composable
fun FeedSkeleton(modifier: Modifier = Modifier, count: Int = 3) {
    Column(
        modifier
            .fillMaxSize()
            .semantics { contentDescription = "Cargando publicaciones" }
            .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpacingL),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingL)
    ) {
        repeat(count) { PostCardSkeleton() }
    }
}
