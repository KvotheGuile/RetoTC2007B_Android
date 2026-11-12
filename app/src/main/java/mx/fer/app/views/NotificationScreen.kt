package mx.fer.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import mx.fer.app.views.components.NotificationCard
import mx.fer.app.views.components.NotificationListSkeleton
import mx.fer.app.views.components.StateContent

@Composable
fun NotificationScreen(
    onClickNotif: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                items(items = state.notifications, key = { it.id }) { notification ->
                    NotificationCard(
                        onClick = { onClickNotif(notification.idPublication) },
                        notificationItem = notification,
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NotificationScreenPreview() {
    FERMovilTheme {
        NotificationScreen(onClickNotif = {})
    }
}
