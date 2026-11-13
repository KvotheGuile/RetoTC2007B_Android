package mx.fer.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.fer.app.R
import mx.fer.app.ui.theme.Dimens
import mx.fer.app.ui.theme.FERMovilTheme
import mx.fer.app.viewmodels.NotificationViewModel
import mx.fer.app.views.components.EmptyState
import mx.fer.app.views.components.NoticeSheet
import mx.fer.app.views.components.NotificationCard
import mx.fer.app.views.components.NotificationListSkeleton
import mx.fer.app.views.components.SectionTitle
import mx.fer.app.views.components.StateContent

@Composable
fun NotificationScreen(
    onOpenPost: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // Notice without a post whose full text is open in the bottom sheet (survives rotation)
    var openNoticeId by rememberSaveable { mutableStateOf<String?>(null) }

    StateContent(
        isLoading = state.isLoading,
        error = state.error,
        onRetry = { viewModel.loadNotifications() },
        modifier = modifier,
        loading = { NotificationListSkeleton() }
    ) {
        if (state.notifications.isEmpty()) {
            EmptyState(
                icon = R.drawable.ic_notifications,
                title = "Sin notificaciones",
                message = "Aquí verás los avisos que envíe la Fundación."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.groups.forEachIndexed { index, (group, notifications) ->
                    item(key = "group_${group.name}") {
                        SectionTitle(
                            text = group.label,
                            modifier = Modifier
                                .animateItem()
                                .padding(top = if (index == 0) 4.dp else 14.dp, bottom = 2.dp)
                        )
                    }
                    items(items = notifications, key = { it.id }) { notification ->
                        NotificationCard(
                            onClick = {
                                viewModel.markAsRead(notification)
                                val postId = notification.idPublication
                                if (postId != null) onOpenPost(postId)
                                else openNoticeId = notification.id
                            },
                            notificationItem = notification,
                            isRead = state.isRead(notification),
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    state.notifications.firstOrNull { it.id == openNoticeId }?.let { notice ->
        NoticeSheet(notification = notice, onDismiss = { openNoticeId = null })
    }
}

@Preview
@Composable
fun NotificationScreenPreview() {
    FERMovilTheme {
        NotificationScreen(onOpenPost = {})
    }
}
