package mx.fer.app.views

import mx.fer.app.views.components.LocalReduceMotion
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.fer.app.model.TextSize
import mx.fer.app.model.ThemeMode
import mx.fer.app.views.components.systemAnimationsOff
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.sp
import mx.fer.app.model.UserPreferences
import mx.fer.app.ui.theme.CappedFontScale
import mx.fer.app.ui.theme.Dimens
import mx.fer.app.viewmodels.UserViewModel
import mx.fer.app.R
import mx.fer.app.views.components.SectionTitle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    preferences: UserPreferences,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding, vertical = 16.dp)
    ) {
        SectionHeader(
            title = "Notificaciones",
            subtitle = "Elige cómo quieres enterarte de las novedades."
        )
        PreferenceSwitchRow(
            title = "Notificaciones push",
            description = "Recibe avisos directamente en tu celular",
            checked = preferences.pushNotificationsEnabled,
            onCheckedChange = userViewModel::setPushNotificationsEnabled
        )

        Spacer(Modifier.height(32.dp))

        SectionHeader(
            title = "Temas de interés",
            subtitle = "Selecciona qué publicaciones te interesan."
        )
        PreferenceSwitchRow(
            title = "Anuncios para becarios",
            description = "Renovaciones, requisitos y recordatorios",
            checked = preferences.interestedScholarship,
            onCheckedChange = userViewModel::setInterestedScholarship
        )
        PreferenceSwitchRow(
            title = "Anuncios para aspirantes",
            description = "Convocatorias y procesos de solicitud",
            checked = preferences.interestedApplicants,
            onCheckedChange = userViewModel::setInterestedApplicants
        )
        PreferenceSwitchRow(
            title = "Avisos generales",
            description = "Eventos y noticias de la fundación",
            checked = preferences.interestedGeneral,
            onCheckedChange = userViewModel::setInterestedGeneral
        )

        val reduceMotion = LocalReduceMotion.current
        val noInterests = !preferences.interestedScholarship &&
                !preferences.interestedApplicants &&
                !preferences.interestedGeneral
        AnimatedVisibility(
            visible = noInterests,
            enter = if (reduceMotion) fadeIn() else expandVertically() + fadeIn(),
            exit = if (reduceMotion) fadeOut() else shrinkVertically() + fadeOut()
        ) {
            NoInterestsWarning(Modifier.padding(top = 16.dp))
        }

        Spacer(Modifier.height(32.dp))

        SectionHeader(
            title = "Apariencia",
            subtitle = "Elige cómo se ve la aplicación."
        )
        Spacer(Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ThemeMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = preferences.themeMode == mode,
                    onClick = { userViewModel.setThemeMode(mode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ThemeMode.entries.size
                    )
                ) {
                    Text(mode.label)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        SectionHeader(
            title = "Accesibilidad",
            subtitle = "Ajusta la app para leerla con más comodidad."
        )
        Spacer(Modifier.height(12.dp))
        TextSizePicker(
            selected = preferences.textSize,
            onSelect = userViewModel::setTextSize
        )
        Spacer(Modifier.height(4.dp))
        PreferenceSwitchRow(
            title = "Reducir movimiento",
            description = "Quita las animaciones que deslizan o agrandan elementos",
            // Until the user decides, reflect Android's own "Remove animations" setting
            checked = preferences.reduceMotion ?: systemAnimationsOff(),
            onCheckedChange = userViewModel::setReduceMotion
        )

        Spacer(Modifier.height(32.dp))
        AboutSection()
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * "Tamaño del texto": three "Aa" of growing size in a segmented row like the theme picker.
 * The whole screen resizes as soon as one is picked, so it works as its own preview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextSizePicker(
    selected: TextSize,
    onSelect: (TextSize) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Tamaño del texto",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        // Name of the current option, since the buttons only show "Aa"
        Text(
            text = selected.label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        )
    }
    Spacer(Modifier.height(8.dp))
    val glyphSizes = listOf(14.sp, 18.sp, 22.sp)
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        TextSize.entries.forEachIndexed { index, size ->
            SegmentedButton(
                selected = selected == size,
                onClick = { onSelect(size) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = TextSize.entries.size),
                // No checkmark: the filled segment already marks the choice, and the icon pushed
                // the "Aa" off-centre in the selected one
                icon = {},
                // Same fixed height for the three, so the big "Aa" doesn't make its segment taller
                modifier = Modifier
                    .height(48.dp)
                    .semantics { contentDescription = "Texto ${size.label.lowercase()}" }
            ) {
                // The glyphs show the three sizes, so they must not grow with the text setting
                CappedFontScale(max = 1f) {
                    Text(
                        text = "Aa",
                        fontSize = glyphSizes[index],
                        lineHeight = glyphSizes[index],
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    SectionTitle(title)
    Spacer(Modifier.height(6.dp))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(4.dp))
}

/** Shown when every interest is off: the user would not receive any notification. */
@Composable
private fun NoInterestsWarning(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error_outline),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "No recibirás ningún aviso",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Activa al menos un tema de interés para enterarte de convocatorias, fechas y novedades.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

private data class AboutLink(
    val label: String,
    val detail: String,
    val url: String,
    @androidx.annotation.DrawableRes val icon: Int
)

// Official links, as published on www.fer.org.mx
private val aboutLinks = listOf(
    AboutLink("Sitio web", "fer.org.mx", "https://www.fer.org.mx", R.drawable.ic_language),
    AboutLink("Llamar", "(662) 212-1453", "tel:6622121453", R.drawable.ic_call),
    AboutLink("Facebook", "fer.org.mx", "https://www.facebook.com/fer.org.mx/", R.drawable.ic_facebook),
    AboutLink("Instagram", "@fundacionespososrodriguez", "https://www.instagram.com/fundacionespososrodriguez/", R.drawable.ic_instagram)
)

/** "Acerca de la Fundación": logo, short description, official links and the app version. */
@Composable
private fun AboutSection() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val version = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrNull() ?: ""
    }

    SectionHeader(
        title = "Acerca de la Fundación",
        subtitle = "Conoce más de la Fundación y síguenos en redes sociales."
    )
    Spacer(Modifier.height(12.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Standard logo at full size (above the manual's 1.5 cm minimum)
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo_fer),
                contentDescription = "Fundación Esposos Rodríguez",
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                "Fundación Esposos Rodríguez",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        aboutLinks.forEach { link ->
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            AboutLinkRow(link) { openLink(context, link.url) }
        }
    }

    Spacer(Modifier.height(20.dp))
    Text(
        text = "FER móvil · versión $version",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun AboutLinkRow(link: AboutLink, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Abrir ${link.label}", onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(link.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(link.label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(link.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            painter = painterResource(R.drawable.ic_open_in_new),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

/** Opens a web link in the browser/app, or the dialer for tel: links. Never crashes if nothing can handle it. */
private fun openLink(context: android.content.Context, url: String) {
    val action = if (url.startsWith("tel:")) android.content.Intent.ACTION_DIAL
    else android.content.Intent.ACTION_VIEW
    runCatching {
        context.startActivity(android.content.Intent(action, android.net.Uri.parse(url)))
    }
}

@Composable
private fun PreferenceSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = checked,
                    role = Role.Switch,
                    onValueChange = onCheckedChange
                )
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(16.dp))
            // onCheckedChange = null: the Row's toggleable handles the click
            Switch(checked = checked, onCheckedChange = null)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}