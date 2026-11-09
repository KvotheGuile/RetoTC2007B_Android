package mx.fer.app.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mx.fer.app.R
import mx.fer.app.ui.theme.Dimens

private val HeaderHeight = 80.dp
private val LogoSize = 56.dp
private val BellButtonSize = 38.dp
private val BellIconSize = 20.dp

@Composable
fun AppHeader(
    title: String,
    onNotificationsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderHeight)
                .padding(horizontal = Dimens.ScreenPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.logo_fer),
                    contentDescription = "Logo de la Fundación",
                    modifier = Modifier.size(LogoSize)
                )
                Spacer(Modifier.width(Dimens.SpacingM))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Box(
                modifier = Modifier
                    .size(BellButtonSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable { onNotificationsClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_notifications),
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(BellIconSize)
                )
            }
        }
    }
}