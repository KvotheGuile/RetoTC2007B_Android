package mx.fer.app.views

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import mx.fer.app.R
import mx.fer.app.model.Post
import mx.fer.app.ui.theme.Dimens
import mx.fer.app.util.toFullDate
import mx.fer.app.viewmodels.PostViewModel
import mx.fer.app.views.components.AttachmentCard
import mx.fer.app.views.components.EmptyState
import mx.fer.app.views.components.PostImage
import mx.fer.app.views.components.SectionTitle
import mx.fer.app.views.components.label

/** Post detail (RF-08): image on top, then channel, title, date and the full text. */
@Composable
fun PostScreen(
    postId: String,
    viewModel: PostViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(postId) { viewModel.selectPost(postId) }

    // Clear the selection when leaving, so the next post doesn't flash the old one
    DisposableEffect(postId) {
        onDispose { viewModel.clearSelectedPost() }
    }

    // Use the already loaded list on the very first frame, so the shared image transition
    // has its target immediately. Fall back to the fetched post (e.g. from a notification).
    val post = state.selectedPost?.takeIf { it.id == postId }
        ?: state.posts.firstOrNull { it.id == postId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            post != null -> PostContent(post)

            state.error != null -> EmptyState(
                icon = R.drawable.ic_error_outline,
                title = "No pudimos abrir la publicación",
                message = "Revisa tu conexión a internet e inténtalo de nuevo.",
                actionLabel = "Reintentar",
                onAction = { viewModel.selectPost(postId) }
            )

            else -> CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        TopActions(
            onBack = onBack,
            post = post,
            overImage = post?.linkImg != null
        )
    }
}

@Composable
private fun PostContent(post: Post) {
    var zoomed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        val imageUrl = post.linkImg
        if (imageUrl != null) {
            Box {
                PostImage(
                    url = imageUrl,
                    postId = post.id,
                    contentDescription = "Imagen de la publicación. Toca para ampliar.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clickable(onClickLabel = "Ampliar imagen") { zoomed = true }
                )
                // Top scrim keeps the status bar and the floating buttons readable
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent)
                            )
                        )
                )
            }
        } else {
            Spacer(Modifier.statusBarsPadding().height(64.dp))
        }

        Column(Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpacingXL)) {
            SectionTitle(post.type.label)
            Spacer(Modifier.height(12.dp))
            Text(
                text = post.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = post.publishedAt.toFullDate(),   // "Hace 3 días · 8 nov 2026"
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            SelectionContainer {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            val attachments = post.attachments.orEmpty()
            if (attachments.isNotEmpty()) {
                Spacer(Modifier.height(28.dp))
                SectionTitle(if (attachments.size == 1) "Archivo adjunto" else "Archivos adjuntos")
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    attachments.forEach { AttachmentCard(it) }
                }
            }
            // TODO: video attachments once the backend defines how they arrive.

            Spacer(Modifier.height(32.dp))
        }
    }

    if (zoomed && post.linkImg != null) {
        ZoomableImageDialog(url = post.linkImg, onDismiss = { zoomed = false })
    }
}

/** Back and share buttons floating over the content. */
@Composable
private fun TopActions(onBack: () -> Unit, post: Post?, overImage: Boolean) {
    val context = LocalContext.current
    val colors = if (overImage) {
        IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black.copy(alpha = 0.35f),
            contentColor = Color.White
        )
    } else {
        IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack, colors = colors) {
            Icon(painterResource(R.drawable.baseline_arrow_back_24), contentDescription = "Volver")
        }
        if (post != null) {
            IconButton(
                colors = colors,
                onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, post.title)
                        putExtra(Intent.EXTRA_TEXT, "${post.title}\n\n${post.description}")
                    }
                    context.startActivity(Intent.createChooser(send, "Compartir publicación"))
                }
            ) {
                Icon(
                    painterResource(R.drawable.ic_share),
                    contentDescription = "Compartir",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/** Full-screen image with pinch to zoom and double tap to reset. */
@Composable
private fun ZoomableImageDialog(url: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
        val transformState = rememberTransformableState { zoom, pan, _ ->
            scale = (scale * zoom).coerceIn(1f, 5f)
            offset = if (scale == 1f) androidx.compose.ui.geometry.Offset.Zero else offset + pan
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = url,
                contentDescription = "Imagen ampliada",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = {
                            scale = 1f
                            offset = androidx.compose.ui.geometry.Offset.Zero
                        })
                    }
                    .transformable(transformState)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(8.dp)
                    .clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                )
            ) {
                Icon(painterResource(R.drawable.ic_close), contentDescription = "Cerrar")
            }
        }
    }
}
