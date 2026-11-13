package mx.fer.app.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import mx.fer.app.R
import mx.fer.app.ui.theme.CappedFontScale
import mx.fer.app.ui.theme.Dimens

private val HeaderHeight = 64.dp

/**
 * Compact top bar: screen title on the left, notifications on the right.
 * The logo no longer lives here: at this size it was below the manual's 1.5 cm minimum.
 * It now appears full size on the splash, onboarding and settings screens.
 */
@Composable
fun AppHeader(
    title: String,
    onNotificationsClick: (() -> Unit)? = null,   // null = hide the bell
    onBackClick: (() -> Unit)? = null,             // null = no arrow
    unreadCount: Int = 0                           // badge on the bell; 0 = no badge
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderHeight)
                .padding(
                    start = if (onBackClick != null) 4.dp else Dimens.ScreenPadding,
                    end = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() }
            )
            if (onNotificationsClick != null) {
                // IconButton gives a 48dp touch target
                IconButton(onClick = onNotificationsClick) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                // Navy instead of the default red: all color comes from #143F90
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    // The badge sits on a 22dp icon; a huge number would cover it
                                    CappedFontScale(max = 1.3f) {
                                        Text(if (unreadCount > 9) "9+" else unreadCount.toString())
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_notifications),
                            contentDescription = when (unreadCount) {
                                0 -> "Notificaciones"
                                1 -> "Notificaciones, 1 sin leer"
                                else -> "Notificaciones, $unreadCount sin leer"
                            },
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
