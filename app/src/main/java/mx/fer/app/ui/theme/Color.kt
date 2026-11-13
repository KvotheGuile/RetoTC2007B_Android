package mx.fer.app.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * FER palette. Everything is a tone of the institutional navy from the identity manual
 * (Pantone 293C / RGB 20-63-144 = #143F90). No extra hues: the manual asks for a sober,
 * single-colour identity, so states and emphasis use lighter/darker tones of the same blue.
 */

// Brand
val FerNavy = Color(0xFF143F90)        // Pantone 293C, the only brand colour
val FerNavyDeep = Color(0xFF0B2A66)    // gradient end / pressed
val FerNavyBright = Color(0xFF2456B8)  // gradient start

// Light scheme
val LightPrimary = FerNavy
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFDCE5F7)
val LightOnPrimaryContainer = Color(0xFF0A1F4A)
val LightBackground = Color(0xFFF4F7FC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceContainer = Color(0xFFEDF1F9)
val LightSurfaceContainerHigh = Color(0xFFE6EBF5)
val LightOnSurface = Color(0xFF13213D)
val LightOnSurfaceVariant = Color(0xFF556378)   // 5.7:1 on background, passes WCAG AA
val LightOutline = Color(0xFF8592A8)
val LightOutlineVariant = Color(0xFFE3E8F1)

// Dark scheme (navy-tinted, never pure black)
val DarkPrimary = Color(0xFF9DB7F0)             // lighter tone of the brand blue, readable on dark
val DarkOnPrimary = Color(0xFF0A1F4A)
val DarkPrimaryContainer = Color(0xFF1E3463)
val DarkOnPrimaryContainer = Color(0xFFDCE5F7)
val DarkBackground = Color(0xFF0B1220)
val DarkSurface = Color(0xFF131C2F)
val DarkSurfaceContainer = Color(0xFF18233A)
val DarkSurfaceContainerHigh = Color(0xFF1F2B45)
val DarkOnSurface = Color(0xFFE4E9F3)
val DarkOnSurfaceVariant = Color(0xFFA3AEC2)
val DarkOutline = Color(0xFF6B7891)
val DarkOutlineVariant = Color(0xFF24304A)

// Single semantic accent, for errors only
val ErrorLight = Color(0xFFB3261E)
val ErrorDark = Color(0xFFF2B8B5)
