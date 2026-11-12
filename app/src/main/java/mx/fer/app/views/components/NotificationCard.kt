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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.fer.app.R
import mx.fer.app.model.Notification
import mx.fer.app.util.toRelativeTime

@Composable
fun NotificationCard(
    onClick: () -> Unit,
    notificationItem: Notification,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction, 0.98f)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick)
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
                    if (standalone) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primaryContainer
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = notificationItem.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = notificationItem.date.toRelativeTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = notificationItem.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
