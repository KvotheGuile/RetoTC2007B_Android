package mx.fer.app.views

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.fer.app.model.ThemeMode
import mx.fer.app.model.UserPreferences
import mx.fer.app.ui.theme.Dimens
import mx.fer.app.viewmodels.UserViewModel

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
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(8.dp))
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