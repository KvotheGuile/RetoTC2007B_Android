package mx.fer.app.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.fer.app.R
import mx.fer.app.model.Notification
import mx.fer.app.util.toRelativeTime

@Composable
fun NotificationCard(
    onClick: () -> Unit,
    notificationItem: Notification,
    modifier: Modifier = Modifier,
    isRead: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction, 0.98f)
            .clip(MaterialTheme.shapes.medium)
            // Unread = light brand blue, read = plain white card. Gray would blend into the
            // page background and read as "disabled".
            .background(
                if (isRead) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.primaryContainer
            )
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
            .semantics { stateDescription = if (isRead) "Leída" else "No leída" }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon by audience: Becarios, Aspirantes, General, or a bell for notices with no post.
        // Notices without a post get a solid navy circle so they stand apart from post notifications.
        val standalone = notificationItem.idPublication == null
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when {
                        standalone -> MaterialTheme.colorScheme.primary
                        isRead -> MaterialTheme.colorScheme.primaryContainer
                        // One step darker than the light-blue card, so the circle stays the same
                        // family instead of reading as a white hole
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            .compositeOver(MaterialTheme.colorScheme.primaryContainer)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    if (standalone) R.drawable.ic_notifications else notificationItem.type.icon
                ),
                contentDescription = if (standalone) "Aviso" else notificationItem.type.label,
                tint = if (standalone) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            val title: @Composable (Modifier) -> Unit = { mod ->
                Text(
                    text = notificationItem.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (isRead) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = mod
                )
            }
            val dateAndDot: @Composable () -> Unit = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notificationItem.date.toRelativeTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isRead) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.primary
                    )
                    if (!isRead) {
                        // "New" dot, the same navy as the rest of the app
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
            // With large text the date beside the title squeezes it until words break,
            // so from 1.25x on the date moves to its own line under the title.
            if (LocalDensity.current.fontScale >= 1.25f) {
                title(Modifier)
                dateAndDot()
            } else {
                Row(verticalAlignment = Alignment.Top) {
                    title(Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.padding(top = 2.dp)) { dateAndDot() }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = notificationItem.message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isRead) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
