package mx.fer.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mx.fer.app.R

/*
 * Open Sans stands in for Tahoma (the manual's primary font, not licensed for apps).
 * Every style Material uses is defined here, so no screen falls back to Roboto.
 * Sizes are larger than before (body 15-16sp) for older users and older phones.
 */
val OpenSans = FontFamily(
    Font(R.font.opensans_regular, FontWeight.Normal),
    Font(R.font.opensans_semibold, FontWeight.SemiBold),
    Font(R.font.opensans_bold, FontWeight.Bold)
)

private fun style(size: Int, line: Int, weight: FontWeight, spacing: Float = 0f) = TextStyle(
    fontFamily = OpenSans,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = spacing.sp
)

val Typography = Typography(
    displaySmall = style(32, 40, FontWeight.Bold),
    headlineLarge = style(30, 38, FontWeight.Bold),
    headlineMedium = style(26, 34, FontWeight.Bold),    // onboarding titles
    headlineSmall = style(24, 32, FontWeight.Bold),     // screen titles, post detail title
    titleLarge = style(20, 28, FontWeight.Bold),        // featured post title
    titleMedium = style(17, 24, FontWeight.SemiBold),   // card titles
    titleSmall = style(15, 22, FontWeight.SemiBold),
    bodyLarge = style(16, 26, FontWeight.Normal),       // post body
    bodyMedium = style(15, 22, FontWeight.Normal),      // descriptions
    bodySmall = style(13, 18, FontWeight.Normal),
    labelLarge = style(14, 20, FontWeight.SemiBold),    // buttons, tabs
    labelMedium = style(12, 16, FontWeight.Bold, 0.8f), // section titles (upper case)
    labelSmall = style(12, 16, FontWeight.Normal)       // dates, bottom bar labels
)
