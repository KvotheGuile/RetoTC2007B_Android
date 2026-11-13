package mx.fer.app.views

import mx.fer.app.views.components.LocalReduceMotion
import mx.fer.app.views.components.itemMotion
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import mx.fer.app.model.Post
import mx.fer.app.model.PostType
import mx.fer.app.model.UserPreferences
import mx.fer.app.ui.theme.Dimens
import mx.fer.app.viewmodels.PostViewModel
import mx.fer.app.views.components.EmptyState
import mx.fer.app.views.components.FerTab
import mx.fer.app.views.components.FerTabs
import mx.fer.app.views.components.PostCard
import mx.fer.app.views.components.StateContent
import mx.fer.app.views.components.icon

private val channels = listOf(PostType.SCHOLARSHIP, PostType.APPLICANTS)
private val tabs = listOf(
    FerTab("Becarios", PostType.SCHOLARSHIP.icon),
    FerTab("Aspirantes", PostType.APPLICANTS.icon)
)

/**
 * Tab that matches the user's interests: Aspirantes if that's the only one they follow,
 * otherwise Becarios (also when they follow both or neither).
 */
fun preferredAnnouncementsPage(prefs: UserPreferences): Int =
    if (prefs.interestedApplicants && !prefs.interestedScholarship) channels.indexOf(PostType.APPLICANTS)
    else channels.indexOf(PostType.SCHOLARSHIP)

/** Scroll and page state, hoisted so they survive opening a post and coming back. */
class AnnouncementsState(val pager: PagerState, val lists: List<LazyListState>)

@Composable
fun rememberAnnouncementsState(initialPage: Int = 0): AnnouncementsState {
    val pager = rememberPagerState(initialPage = initialPage) { channels.size }
    val lists = channels.map { rememberLazyListState() }
    return remember(pager) { AnnouncementsState(pager, lists) }
}

/** Becarios (RF-05) and Aspirantes (RF-06), swipeable between each other. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = viewModel(key = "announcements"),
    screenState: AnnouncementsState = rememberAnnouncementsState(),
    onPostClick: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val pager = screenState.pager
    val reduceMotion = LocalReduceMotion.current

    Column(modifier = modifier.fillMaxSize()) {
        FerTabs(
            options = tabs,
            selectedIndex = pager.currentPage,
            onSelect = {
                scope.launch {
                    if (reduceMotion) pager.scrollToPage(it) else pager.animateScrollToPage(it)
                }
            },
            modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 8.dp)
        )

        StateContent(
            isLoading = state.isLoading,
            error = state.error,
            onRetry = { viewModel.loadPosts() }
        ) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize()
            ) {
                HorizontalPager(
                    state = pager,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 1
                ) { page ->
                    val type = channels[page]
                    ChannelList(
                        posts = state.posts.filter { it.type == type },
                        listState = screenState.lists[page],
                        emptyLabel = if (type == PostType.SCHOLARSHIP) "becarios" else "aspirantes",
                        emptyIcon = type.icon,
                        onPostClick = onPostClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelList(
    posts: List<Post>,
    listState: LazyListState,
    emptyLabel: String,
    emptyIcon: Int,
    onPostClick: (String) -> Unit
) {
    if (posts.isEmpty()) {
        EmptyState(
            icon = emptyIcon,
            title = "Aún no hay anuncios",
            message = "Las publicaciones para $emptyLabel aparecerán aquí."
        )
        return
    }
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
        items(posts, key = { it.id }) { post ->
            PostCard(post = post, onClick = onPostClick, modifier = itemMotion())
        }
    }
}
