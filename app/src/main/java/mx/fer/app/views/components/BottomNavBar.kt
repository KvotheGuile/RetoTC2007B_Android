package mx.fer.app.views.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

private val NavBarHeight = 76.dp
private val NavIconSize = 26.dp

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
    val color = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(
                if (isSelected) destination.activeIcon else destination.inactiveIcon
            ),
            contentDescription = destination.label,
            tint = color,
            modifier = Modifier.size(NavIconSize)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = destination.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}