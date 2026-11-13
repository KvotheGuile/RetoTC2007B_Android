package mx.fer.app.views.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.fer.app.R
import mx.fer.app.ui.theme.CappedFontScale

private val NavBarHeight = 76.dp
private val NavIconSize = 28.dp

enum class Destination(
    val label: String, // texto visible, en español
    @DrawableRes val activeIcon: Int,
    @DrawableRes val inactiveIcon: Int
) {
    ANNOUNCEMENTS("Anuncios", R.drawable.ic_announcements_filled, R.drawable.ic_announcements_outline),
    HOME("Inicio", R.drawable.ic_home_filled, R.drawable.ic_home_outline),
    PREFERENCES("Preferencias", R.drawable.ic_preferences_filled, R.drawable.ic_preferences_outline)
}

@Composable
fun BottomNavBar(
    selected: Destination,
    onSelect: (Destination) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(NavBarHeight)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Destination.entries.forEach { destination ->
                NavItem(
                    destination = destination,
                    isSelected = destination == selected,
                    onClick = { onSelect(destination) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    destination: Destination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "navColor"
    )
    // Pill behind the selected icon grows in from the centre
    val pillWidth by animateDpAsState(
        targetValue = if (isSelected) 64.dp else 28.dp,
        animationSpec = motionSpec(spring(stiffness = Spring.StiffnessMediumLow)),
        label = "navPill"
    )
    val pillAlpha by animateFloatAsState(if (isSelected) 1f else 0f, label = "navPillAlpha")

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .selectable(
                selected = isSelected,
                role = Role.Tab,
                interactionSource = null,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(68.dp)
                .height(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .width(pillWidth)
                    .fillMaxHeight()
                    .graphicsLayer { alpha = pillAlpha }
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
            Icon(
                painter = painterResource(
                    if (isSelected) destination.activeIcon else destination.inactiveIcon
                ),
                contentDescription = null,   // the label below already names it
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else color,
                modifier = Modifier.size(NavIconSize)
            )
        }
        Spacer(Modifier.height(4.dp))
        // Three fixed-width slots: past 1.5x "Preferencias" no longer fits on one line
        CappedFontScale(max = 1.5f) {
            Text(
                text = destination.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = color,
                maxLines = 1
            )
        }
    }
}