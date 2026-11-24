package mx.fer.app.views.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import mx.fer.app.R
import mx.fer.app.model.Attachment

/**
 * A PDF linked from a post. Tapping it opens the URL in the browser; the app doesn't
 * download or render the file itself.
 */
@Composable
fun AttachmentCard(
    attachment: Attachment,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interaction = remember { MutableInteractionSource() }
    val name = attachment.name.removeSuffix(".pdf").removeSuffix(".PDF")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction, 0.98f)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                role = Role.Button,
                onClickLabel = "Abrir PDF"
            ) {
                openInBrowser(context, attachment.url)
            }
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_description),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "PDF",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_open_in_new),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Forces the browser, otherwise Drive links open the Drive app and it closes without a Google account
private fun openInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())

    // Default browser = whatever opens a plain https link
    val browser = context.packageManager.resolveActivity(
        Intent(Intent.ACTION_VIEW, "https://".toUri()),
        PackageManager.MATCH_DEFAULT_ONLY
    )
    val browserPackage = browser?.activityInfo?.packageName
    // "android" = no default browser, let the user pick
    if (browserPackage != null && browserPackage != "android") {
        intent.setPackage(browserPackage)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No hay una app para abrir este archivo", Toast.LENGTH_SHORT).show()
    }
}
