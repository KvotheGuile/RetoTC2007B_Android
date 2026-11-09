package mx.fer.app.views.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mx.fer.app.R
import mx.fer.app.model.Notification
import java.util.Date
import mx.fer.app.util.toRelativeTime

@Composable
fun NotificationCard(
    onClick: () -> Unit,
    notificationItem: Notification
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors =

            CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var contentText = notificationItem.message

            if (contentText.length > 150){
                contentText = contentText.substring(0, 125) + "...";
            }

            Icon(
                painter = painterResource(id = R.drawable.baseline_notifications_24), // Your XML file name
                contentDescription = "Search icon",
                //tint = Color.Unspecified // Keeps the original colors, OR use a custom color:
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notificationItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = notificationItem.date.toRelativeTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = contentText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
