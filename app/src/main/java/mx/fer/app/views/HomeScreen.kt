package mx.fer.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.fer.app.model.Post
import mx.fer.app.ui.theme.Dimens
import mx.fer.app.util.toRelativeTime
import mx.fer.app.viewmodels.PostViewModel
import mx.fer.app.views.components.PostItem
import mx.fer.app.views.components.StateContent

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = viewModel(key = "home"),
    onPostClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    StateContent(
        isLoading = state.isLoading,
        error = state.error,
        onRetry = { viewModel.loadPosts() },
        modifier = modifier
    ) {
        HomeContent(state.posts, onPostClick)
    }
}

@Composable
private fun HomeContent(posts: List<Post>,
                        onPostClick: (String) -> Unit) {
    if (posts.isEmpty()) {
        Text(
            text = "Aún no hay publicaciones",
            modifier = Modifier.padding(Dimens.ScreenPadding),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val latest = posts.first()
    val recent = posts.drop(1).take(5)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding, vertical = 20.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Lo más reciente",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                PostItem(
                    title = latest.title,
                    description = latest.description,
                    image = latest.linkImg,
                    date = latest.publishedAt.toRelativeTime(),
                    imageFirst = true,
                    id = latest.id,
                    onClick = onPostClick
                )
            }
        }

        if (recent.isNotEmpty()) {
            item {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 22.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = "Publicaciones recientes",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            items(recent, key = { it.id }) { post ->
                PostItem(
                    title = post.title,
                    description = post.description,
                    image = post.linkImg,
                    date = post.publishedAt.toRelativeTime(),
                    modifier = Modifier.padding(bottom = Dimens.SpacingXL),
                    id = post.id,
                    onClick = onPostClick
                )
            }
        }
    }
}