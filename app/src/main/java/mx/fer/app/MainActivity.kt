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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.fer.app.model.UserPreferences
import mx.fer.app.viewmodels.PostViewModel
import mx.fer.app.viewmodels.UserViewModel
import mx.fer.app.views.LoginScreen
import mx.fer.app.views.PostScreen
import mx.fer.app.views.PreferencesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FERMovilTheme {
                val userViewModel: UserViewModel = viewModel()
                val userState by userViewModel.uiState.collectAsStateWithLifecycle()
                val prefs = userState.preferences


                when {
                    userState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }

                    prefs == null -> {
                        LoginScreen(onContinuar = userViewModel::completeSetup)
                    }

                    else -> {
                        MainContent(prefs, userViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(
    prefs: UserPreferences,
    userViewModel: UserViewModel
){
    val postViewModel: PostViewModel = viewModel()   // one shared instance

    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }
    var showNotifications by rememberSaveable { mutableStateOf(false) }
    var openedPostId by rememberSaveable { mutableStateOf<String?>(null) }

    // Back action for the header arrow and the system back button.
    // null means we're on a root tab and there's nothing to go back to.
    val onBack: (() -> Unit)? = when {
        openedPostId != null -> { { openedPostId = null } }
        showNotifications -> { { showNotifications = false } }
        else -> null
    }
    BackHandler(enabled = onBack != null) { onBack?.invoke() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                title = when {
                    openedPostId != null -> "Publicación"
                    showNotifications -> "Notificaciones"
                    else -> destination.label
                },
                onNotificationsClick = {
                    openedPostId = null
                    showNotifications = true
                },
                onBackClick = onBack
            )
        },
        bottomBar = {
            // Hidden only while a post is open
            if (openedPostId == null) {
                BottomNavBar(
                    selected = destination,
                    onSelect = {
                        destination = it
                        showNotifications = false
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        val postId = openedPostId

        when {
            postId != null -> PostScreen(
                postId = postId,
                viewModel = postViewModel,
                modifier = contentModifier.navigationBarsPadding()
            )

            showNotifications -> NotificationScreen(
                modifier = contentModifier,
                onClickNotif = { id ->
                    if (id != null) openedPostId = id   // null = notification without a post
                }
            )

            else -> when (destination) {
                Destination.HOME -> HomeScreen(
                    modifier = contentModifier,
                    onPostClick = { openedPostId = it }
                )
                Destination.ANNOUNCEMENTS -> AnnouncementsScreen(
                    modifier = contentModifier,
                    onPostClick = { openedPostId = it }
                )
                Destination.PREFERENCES -> PreferencesScreen(
                    preferences = prefs,
                    onPushChange = userViewModel::setPushNotificationsEnabled,
                    onScholarshipChange = userViewModel::setInterestedScholarship,
                    onApplicantsChange = userViewModel::setInterestedApplicants,
                    onGeneralChange = userViewModel::setInterestedGeneral,
                    modifier = contentModifier
                )
            }
        }
    }
}