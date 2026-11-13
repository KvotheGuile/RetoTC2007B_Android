package mx.fer.app

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.fer.app.model.ThemeMode
import mx.fer.app.model.UserPreferences
import mx.fer.app.ui.theme.FERMovilTheme
import mx.fer.app.ui.theme.isDark
import mx.fer.app.viewmodels.NotificationViewModel
import mx.fer.app.viewmodels.PostViewModel
import mx.fer.app.viewmodels.UserViewModel
import mx.fer.app.views.AnnouncementsScreen
import mx.fer.app.views.AnnouncementsState
import mx.fer.app.views.HomeScreen
import mx.fer.app.views.NotificationScreen
import mx.fer.app.views.OnboardingScreen
import mx.fer.app.views.PostScreen
import mx.fer.app.views.PreferencesScreen
import mx.fer.app.views.components.AppHeader
import mx.fer.app.views.components.BottomNavBar
import mx.fer.app.views.components.Destination
import mx.fer.app.views.components.LocalNavAnimatedScope
import mx.fer.app.views.components.LocalSharedTransitionScope
import mx.fer.app.views.preferredAnnouncementsPage
import mx.fer.app.views.rememberAnnouncementsState

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Keep the branded splash until we know whether to show onboarding or the feed
        splash.setKeepOnScreenCondition { userViewModel.uiState.value.isLoading }
        enableEdgeToEdge()

        setContent {
            val userState by userViewModel.uiState.collectAsStateWithLifecycle()
            val prefs = userState.preferences
            val darkTheme = (prefs?.themeMode ?: ThemeMode.SYSTEM).isDark()

            // Status/navigation bar icons follow the app theme, not only the system one
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT
                    ) { darkTheme }
                )
                onDispose {}
            }

            FERMovilTheme(darkTheme = darkTheme) {
                when {
                    // The splash screen covers this state
                    userState.isLoading -> Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )

                    prefs == null -> OnboardingScreen(
                        onFinish = { interests, pushEnabled ->
                            userViewModel.completeOnboarding(interests, pushEnabled)
                        }
                    )

                    else -> MainContent(prefs, userViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainContent(
    prefs: UserPreferences,
    userViewModel: UserViewModel
) {
    val postViewModel: PostViewModel = viewModel()   // one shared instance for the detail screen
    val notificationViewModel: NotificationViewModel = viewModel()   // feeds the bell badge and the list
    val notificationState by notificationViewModel.uiState.collectAsStateWithLifecycle()

    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }
    var showNotifications by rememberSaveable { mutableStateOf(false) }
    var openedPostId by rememberSaveable { mutableStateOf<String?>(null) }

    // Hoisted so scroll position and selected tab survive opening a post and coming back
    val homeListState = rememberLazyListState()
    val preferredPage = preferredAnnouncementsPage(prefs)
    val announcementsState = rememberAnnouncementsState(initialPage = preferredPage)
    // If the user changes their interests later, Anuncios follows on the next visit.
    // Skipped on the first run so it doesn't undo a tab restored after rotation.
    var lastPreferredPage by rememberSaveable { mutableIntStateOf(preferredPage) }
    LaunchedEffect(preferredPage) {
        if (preferredPage != lastPreferredPage) {
            lastPreferredPage = preferredPage
            announcementsState.pager.requestScrollToPage(preferredPage)
        }
    }

    BackHandler(enabled = openedPostId != null) { openedPostId = null }
    BackHandler(enabled = openedPostId == null && showNotifications) { showNotifications = false }

    // Shared element transition: the post image grows from the card into the detail screen
    SharedTransitionLayout {
        AnimatedContent(
            targetState = openedPostId,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label = "postDetail"
        ) { postId ->
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this@SharedTransitionLayout,
                LocalNavAnimatedScope provides this
            ) {
                if (postId != null) {
                    PostScreen(
                        postId = postId,
                        viewModel = postViewModel,
                        onBack = { openedPostId = null }
                    )
                } else {
                    MainScaffold(
                        prefs = prefs,
                        userViewModel = userViewModel,
                        notificationViewModel = notificationViewModel,
                        unreadCount = notificationState.unreadCount,
                        destination = destination,
                        onDestinationChange = {
                            destination = it
                            showNotifications = false
                        },
                        showNotifications = showNotifications,
                        onShowNotificationsChange = { showNotifications = it },
                        homeListState = homeListState,
                        announcementsState = announcementsState,
                        onOpenPost = { openedPostId = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(
    prefs: UserPreferences,
    userViewModel: UserViewModel,
    notificationViewModel: NotificationViewModel,
    unreadCount: Int,
    destination: Destination,
    onDestinationChange: (Destination) -> Unit,
    showNotifications: Boolean,
    onShowNotificationsChange: (Boolean) -> Unit,
    homeListState: LazyListState,
    announcementsState: AnnouncementsState,
    onOpenPost: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                title = if (showNotifications) "Notificaciones" else destination.label,
                onNotificationsClick = if (showNotifications) null else {
                    { onShowNotificationsChange(true) }
                },
                onBackClick = if (showNotifications) {
                    { onShowNotificationsChange(false) }
                } else null,
                unreadCount = unreadCount
            )
        },
        bottomBar = {
            BottomNavBar(selected = destination, onSelect = onDestinationChange)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        val screen: Destination? = if (showNotifications) null else destination

        Crossfade(targetState = screen, label = "tabs") { target ->
            when (target) {
                null -> NotificationScreen(
                    modifier = contentModifier,
                    viewModel = notificationViewModel,
                    onOpenPost = onOpenPost   // notices without a post open a sheet inside the screen
                )
                Destination.HOME -> HomeScreen(
                    modifier = contentModifier,
                    listState = homeListState,
                    onPostClick = onOpenPost
                )
                Destination.ANNOUNCEMENTS -> AnnouncementsScreen(
                    modifier = contentModifier,
                    screenState = announcementsState,
                    onPostClick = onOpenPost
                )
                Destination.PREFERENCES -> PreferencesScreen(
                    preferences = prefs,
                    userViewModel = userViewModel,
                    modifier = contentModifier
                )
            }
        }
    }
}
