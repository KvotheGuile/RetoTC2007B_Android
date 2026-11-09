package mx.fer.app.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.fer.app.model.Notification
import mx.fer.app.ui.theme.FERMovilTheme
import mx.fer.app.views.components.NotificationCard

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import mx.fer.app.viewmodels.NotificationViewModel

@Composable
fun NotificationScreen(
    onClickNotif: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: ${state.error}")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.loadNotifications() }) { Text("Reintentar") }
            }

            state.notifications.isEmpty() -> Text("No hay notificaciones")

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = state.notifications, key = { it.id }) { notification ->
                    NotificationCard(
                        onClick = { onClickNotif(notification.idPublication) },
                        notificationItem = notification
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NotificationScreenPreview(){
    FERMovilTheme()
    {
        NotificationScreen(
            onClickNotif = {it -> },
        )
    }
}