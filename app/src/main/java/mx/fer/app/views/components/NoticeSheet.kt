package mx.fer.app.views.components

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mx.fer.app.R
import mx.fer.app.model.Notification
import mx.fer.app.util.toFullDate
import java.util.regex.Pattern

/**
 * Full text of a notice that has no post behind it.
 * Opens at the height of its content (no half-open step); long text scrolls inside and the
 * sheet stops short of the status bar. It closes by dragging the handle down, tapping the
 * dimmed list, the back gesture, or the "Entendido" button for people who don't know the swipe.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeSheet(
    notification: Notification,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notifications),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        text = notification.date.toFullDate(),
                        style = MaterialTheme.typography.bodySmall,   // labelMedium is the bold, spaced section-title style
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = linkify(notification.message, MaterialTheme.colorScheme.primary),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    // Animate the sheet down first, then remove it
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text("Entendido")
            }
        }
    }
}

/** Makes e-mails, web addresses and phone numbers in the notice tappable. */
private fun linkify(text: String, linkColor: Color): AnnotatedString {
    val found = mutableListOf<Triple<Int, Int, String>>()

    fun scan(pattern: Pattern, accept: (String) -> Boolean = { true }, toUrl: (String) -> String) {
        val m = pattern.matcher(text)
        while (m.find()) {
            val start = m.start()
            val end = m.end()
            val overlaps = found.any { start < it.second && end > it.first }
            if (!overlaps && accept(m.group())) found += Triple(start, end, toUrl(m.group()))
        }
    }

    // E-mails first so their domain isn't picked up as a web address
    scan(Patterns.EMAIL_ADDRESS) { "mailto:$it" }
    scan(Patterns.WEB_URL) { if (it.startsWith("http", ignoreCase = true)) it else "https://$it" }
    // At least 8 digits, so years like "2026" don't become phone links
    scan(Patterns.PHONE, accept = { s -> s.count(Char::isDigit) >= 8 }) { s ->
        "tel:" + s.filter { it.isDigit() || it == '+' }
    }

    val style = TextLinkStyles(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline))
    return buildAnnotatedString {
        append(text)
        found.forEach { (start, end, url) -> addLink(LinkAnnotation.Url(url, style), start, end) }
    }
}
