package mx.fer.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.fer.app.model.PostType
import mx.fer.app.ui.theme.Dimens
import mx.fer.app.util.toRelativeTime
import mx.fer.app.viewmodels.PostViewModel
import mx.fer.app.views.components.FerTabs
import mx.fer.app.views.components.PostItem
import mx.fer.app.views.components.StateContent

private val tabs = listOf(
    "Becarios" to PostType.SCHOLARSHIP,
    "Aspirantes" to PostType.APPLICANTS
)

@Composable
fun AnnouncementsScreen(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = viewModel(key = "announcements")
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val type = tabs[selectedTab].second

    LaunchedEffect(selectedTab) {
        if (viewModel.uiState.value.selectedType != type) {
            viewModel.onTypeSelected(type)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        FerTabs(
            options = tabs.map { it.first },
            selectedIndex = selectedTab,
            onSelect = { selectedTab = it }
        )

        StateContent(
            isLoading = state.isLoading,
            error = state.error,
            onRetry = { viewModel.onTypeSelected(type) }
        ) {

            val posts = state.posts.filter { it.type == type }

            if (posts.isEmpty()) {
                Text(
                    text = "Aún no hay anuncios",
                    modifier = Modifier.padding(Dimens.ScreenPadding),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = Dimens.ScreenPadding,
                        vertical = 16.dp
                    )
                ) {
                    itemsIndexed(posts, key = { _, post -> post.id }) { index, post ->
                        Column {
                            PostItem(
                                title = post.title,
                                description = post.description,
                                image = post.linkImg,
                                date = post.publishedAt.toRelativeTime(),
                                descriptionMaxLines = 2
                            )
                            if (index < posts.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}