package mx.fer.app.views.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mx.fer.app.R

private enum class ContentState { LOADING, ERROR, CONTENT }

/**
 * Loading / error / content switcher. The raw error message is never shown to the user
 * (it can leak technical details); a friendly message with a retry button is shown instead.
 */
@Composable
fun StateContent(
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loading: @Composable () -> Unit = { FeedSkeleton() },
    content: @Composable () -> Unit
) {
    val state = when {
        isLoading -> ContentState.LOADING
        error != null -> ContentState.ERROR
        else -> ContentState.CONTENT
    }
    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(targetState = state, label = "stateContent") { s ->
            when (s) {
                ContentState.LOADING -> loading()
                ContentState.ERROR -> EmptyState(
                    icon = R.drawable.ic_error_outline,
                    title = "No pudimos cargar el contenido",
                    message = "Revisa tu conexión a internet e inténtalo de nuevo.",
                    actionLabel = "Reintentar",
                    onAction = onRetry
                )
                ContentState.CONTENT -> content()
            }
        }
    }
}

/** Centered icon + message, used for empty lists and errors. */
@Composable
fun EmptyState(
    @DrawableRes icon: Int,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            FilledTonalButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}
