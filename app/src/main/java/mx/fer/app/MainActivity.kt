package mx.fer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mx.fer.app.ui.theme.FERMovilTheme
import mx.fer.app.views.AnnouncementsScreen
import mx.fer.app.views.HomeScreen
import mx.fer.app.views.NotificationScreen
import mx.fer.app.views.components.AppHeader
import mx.fer.app.views.components.BottomNavBar
import mx.fer.app.views.components.Destination
import androidx.activity.compose.BackHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FERMovilTheme {
                var destination by rememberSaveable { mutableStateOf(Destination.HOME) }
                var showNotifications by rememberSaveable { mutableStateOf(false) }

                // System back button closes notifications instead of exiting the app
                BackHandler(enabled = showNotifications) {
                    showNotifications = false
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppHeader(
                            title = if (showNotifications) "Notificaciones" else destination.label,
                            onNotificationsClick = { showNotifications = true }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            selected = destination,
                            onSelect = {
                                destination = it
                                showNotifications = false // leaving notifications when a tab is tapped
                            }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    val contentModifier = Modifier.padding(innerPadding)

                    if (showNotifications) {
                        NotificationScreen(
                            modifier = contentModifier,
                            onClickNotif = { postId ->
                                // TODO: open the post detail
                            }
                        )
                    } else {
                        when (destination) {
                            Destination.HOME -> HomeScreen(modifier = contentModifier)
                            Destination.ANNOUNCEMENTS -> AnnouncementsScreen(modifier = contentModifier)
                            Destination.PREFERENCES -> Box(
                                modifier = contentModifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = destination.label,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}