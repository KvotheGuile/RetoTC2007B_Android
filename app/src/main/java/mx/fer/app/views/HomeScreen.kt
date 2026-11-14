package mx.fer.app.views

import mx.fer.app.views.components.itemMotion
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.fer.app.R
import mx.fer.app.model.Post
import mx.fer.app.model.PostType
import mx.fer.app.ui.theme.Dimens
import mx.fer.app.viewmodels.PostViewModel
import mx.fer.app.views.components.EmptyState
import mx.fer.app.views.components.FeaturedPostCard
import mx.fer.app.views.components.PostCard
import mx.fer.app.views.components.SectionTitle
import mx.fer.app.views.components.StateContent

/** Home = "Anuncios generales" channel (RF-04): general posts, newest first (RF-07). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = viewModel(),
    listState: LazyListState = rememberLazyListState(),
    onPostClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val posts = state.posts.filter { it.type == PostType.GENERAL }

    StateContent(
        isLoading = state.isLoading,
        error = state.error,
        onRetry = { viewModel.loadPosts() },
        modifier = modifier
    ) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize()
        ) {
            HomeContent(posts, listState, onPostClick)
        }
    }
}

@Composable
private fun HomeContent(
    posts: List<Post>,
    listState: LazyListState,
    onPostClick: (String) -> Unit
) {
    if (posts.isEmpty()) {
        EmptyState(
            icon = R.drawable.ic_general_announcement,
            title = "Aún no hay anuncios",
            message = "Cuando la Fundación publique algo nuevo, lo verás aquí."
        )
        return
    }

    val latest = posts.first()
    val recent = posts.drop(1)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.ScreenPadding,
            end = Dimens.ScreenPadding,
            top = 8.dp,
            bottom = Dimens.SpacingXL
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingL)
    ) {
        item(key = "title-latest") { SectionTitle("Lo más reciente") }
        item(key = latest.id) {
            FeaturedPostCard(post = latest, onClick = onPostClick, modifier = itemMotion())
        }

        if (recent.isNotEmpty()) {
            item(key = "title-recent") {
                SectionTitle("Publicaciones recientes", Modifier.padding(top = Dimens.SpacingS))
            }
            items(recent, key = { it.id }) { post ->
                PostCard(post = post, onClick = onPostClick, modifier = itemMotion())
            }
        }
    }
}
