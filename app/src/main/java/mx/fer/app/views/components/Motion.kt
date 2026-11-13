package mx.fer.app.views.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import android.provider.Settings
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/*
 * "Reducir movimiento" (Preferencias > Accesibilidad). When on, anything that slides, grows or
 * bounces is switched off; fades and color changes stay, since they don't cause motion sickness.
 */
val LocalReduceMotion = staticCompositionLocalOf { false }

/** True when Android's "Remove animations" is on (animator duration scale = 0). */
@Composable
fun systemAnimationsOff(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/** [spec] normally, an instant jump when reduce motion is on. For movement, not for fades. */
@Composable
fun <T> motionSpec(spec: AnimationSpec<T>): AnimationSpec<T> =
    if (LocalReduceMotion.current) snap() else spec

/** List items fade in and out as usual but don't slide to their new place when reduce motion is on. */
@Composable
fun LazyItemScope.itemMotion(): Modifier =
    if (LocalReduceMotion.current) Modifier.animateItem(placementSpec = null)
    else Modifier.animateItem()

/*
 * Shared-element plumbing. MainActivity provides both scopes; any image can then call
 * Modifier.sharedPostImage(postId) and it will animate between the feed and the detail.
 * When a scope is missing (previews, onboarding) the modifier does nothing.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedPostImage(postId: String): Modifier {
    if (LocalReduceMotion.current) return this   // no growing image; the detail just fades in
    val shared = LocalSharedTransitionScope.current ?: return this
    val animated = LocalNavAnimatedScope.current ?: return this
    return with(shared) {
        this@sharedPostImage.sharedElement(
            sharedContentState = rememberSharedContentState(key = "post-image-$postId"),
            animatedVisibilityScope = animated
        )
    }
}

/** Slight shrink while pressed, so cards feel tactile. */
@Composable
fun Modifier.pressScale(interactionSource: InteractionSource, pressedScale: Float = 0.97f): Modifier {
    if (LocalReduceMotion.current) return this
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pressScale"
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
