package mx.fer.app.views

import androidx.compose.animation.core.spring
import mx.fer.app.views.components.motionSpec
import mx.fer.app.views.components.LocalReduceMotion
import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.fer.app.R
import mx.fer.app.model.PostType
import mx.fer.app.ui.theme.FERMovilTheme
import mx.fer.app.ui.theme.FerNavy
import mx.fer.app.ui.theme.FerNavyBright
import mx.fer.app.ui.theme.FerNavyDeep
import mx.fer.app.views.components.icon
import mx.fer.app.views.components.pressScale

private data class Interest(val type: PostType, val title: String, val description: String)

private val interests = listOf(
    Interest(PostType.GENERAL, "Anuncios generales", "Eventos, noticias y avisos de la Fundación"),
    Interest(PostType.SCHOLARSHIP, "Becarios", "Renovaciones, requisitos y recordatorios"),
    Interest(PostType.APPLICANTS, "Aspirantes", "Convocatorias y procesos de solicitud")
)

/**
 * First-run setup (RF-01, RF-09): welcome, pick interests (at least one), then the
 * notification permission. No account and no personal data are requested.
 */
@Composable
fun OnboardingScreen(
    onFinish: (interests: Set<PostType>, pushEnabled: Boolean) -> Unit
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableStateOf(setOf<PostType>()) }

    BackHandler(enabled = step > 0) { step-- }

    val reduceMotion = LocalReduceMotion.current
    AnimatedContent(
        targetState = step,
        transitionSpec = {
            if (reduceMotion) return@AnimatedContent fadeIn() togetherWith fadeOut()
            val forward = targetState > initialState
            (slideInHorizontally { if (forward) it / 3 else -it / 3 } + fadeIn()) togetherWith
                    (slideOutHorizontally { if (forward) -it / 3 else it / 3 } + fadeOut())
        },
        label = "onboardingStep"
    ) { current ->
        when (current) {
            0 -> WelcomeStep(onStart = { step = 1 })
            1 -> InterestsStep(
                selected = selected,
                onToggle = { type ->
                    selected = if (type in selected) selected - type else selected + type
                },
                onContinue = { step = 2 }
            )
            else -> NotificationsStep(
                onDone = { pushEnabled -> onFinish(selected, pushEnabled) }
            )
        }
    }
}

/* ---------- Step 1: welcome ---------- */

@Composable
private fun WelcomeStep(onStart: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        BrandBackground(Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            // Standard logo, well above the manual's 1.5 cm minimum, with generous clear space
            Image(
                painter = painterResource(R.drawable.logo_fer),
                contentDescription = "Fundación Esposos Rodríguez",
                modifier = Modifier.size(150.dp)
            )
            Spacer(Modifier.height(48.dp))
            Text(
                text = "Toda la información de la Fundación, en un solo lugar",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Avisos, convocatorias y fechas importantes, directo en tu celular.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = FerNavy
                )
            ) {
                Text("Comenzar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/**
 * Navy gradient with faint angular facets, taken from the slide templates in the
 * identity manual.
 */
@Composable
private fun BrandBackground(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawRect(
            Brush.linearGradient(
                colors = listOf(FerNavyBright, FerNavy, FerNavyDeep),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )
        )
        val facet = Color.White.copy(alpha = 0.05f)
        drawPath(
            Path().apply {
                moveTo(0f, size.height * 0.62f)
                lineTo(size.width * 0.55f, size.height * 0.80f)
                lineTo(0f, size.height)
                close()
            },
            facet
        )
        drawPath(
            Path().apply {
                moveTo(size.width, size.height * 0.70f)
                lineTo(size.width * 0.35f, size.height)
                lineTo(size.width, size.height)
                close()
            },
            facet
        )
        drawPath(
            Path().apply {
                moveTo(size.width * 0.6f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.25f)
                close()
            },
            Color.White.copy(alpha = 0.04f)
        )
    }
}

/* ---------- Step 2: interests ---------- */

@Composable
private fun InterestsStep(
    selected: Set<PostType>,
    onToggle: (PostType) -> Unit,
    onContinue: () -> Unit
) {
    StepScaffold(
        stepIndex = 1,
        primaryLabel = "Continuar",
        primaryEnabled = selected.isNotEmpty(),
        onPrimary = onContinue
    ) {
        Text(
            text = "¿Qué te interesa?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Elige al menos una opción. Recibirás avisos solo de lo que elijas y puedes cambiarlo después en Preferencias.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))
        interests.forEach { interest ->
            InterestCard(
                title = interest.title,
                description = interest.description,
                icon = interest.type.icon,
                checked = interest.type in selected,
                onToggle = { onToggle(interest.type) }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun InterestCard(
    title: String,
    description: String,
    @DrawableRes icon: Int,
    checked: Boolean,
    onToggle: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val colors = MaterialTheme.colorScheme
    val borderColor by animateColorAsState(
        if (checked) colors.primary else colors.outlineVariant, label = "interestBorder"
    )
    val container by animateColorAsState(
        if (checked) colors.primaryContainer else colors.surface, label = "interestBg"
    )
    val borderWidth by animateDpAsState(if (checked) 2.dp else 1.dp, label = "interestBorderW")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interaction)
            .clip(MaterialTheme.shapes.large)
            .background(container)
            .border(BorderStroke(borderWidth, borderColor), MaterialTheme.shapes.large)
            .toggleable(
                value = checked,
                interactionSource = interaction,
                indication = ripple(),
                role = Role.Checkbox,
                onValueChange = { onToggle() }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (checked) colors.primary else colors.surfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (checked) colors.onPrimary else colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
            Text(description, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .then(
                    if (checked) Modifier.background(colors.primary)
                    else Modifier.border(2.dp, colors.outline, CircleShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/* ---------- Step 3: notifications ---------- */

@Composable
private fun NotificationsStep(onDone: (pushEnabled: Boolean) -> Unit) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> onDone(granted) }

    StepScaffold(
        stepIndex = 2,
        primaryLabel = "Activar notificaciones",
        primaryEnabled = true,
        onPrimary = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onDone(true)   // before Android 13 notifications don't need a runtime permission
            }
        },
        secondaryLabel = "Ahora no",
        onSecondary = { onDone(false) }
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_notifications),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(Modifier.height(32.dp))
        Text(
            text = "No te pierdas ningún aviso",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Te avisaremos cuando haya nuevas publicaciones, fechas límite o recordatorios de los temas que elegiste.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/* ---------- Shared layout ---------- */

@Composable
private fun StepScaffold(
    stepIndex: Int,
    primaryLabel: String,
    primaryEnabled: Boolean,
    onPrimary: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        StepDots(current = stepIndex, total = 3)
        Spacer(Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            content = content
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onPrimary,
            enabled = primaryEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(primaryLabel, style = MaterialTheme.typography.labelLarge)
        }
        if (secondaryLabel != null && onSecondary != null) {
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = onSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(secondaryLabel, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun StepDots(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            val width by animateDpAsState(
                if (i == current) 24.dp else 8.dp,
                animationSpec = motionSpec(spring()),
                label = "dot"
            )
            Box(
                Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(
                        if (i <= current) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    FERMovilTheme { OnboardingScreen(onFinish = { _, _ -> }) }
}
