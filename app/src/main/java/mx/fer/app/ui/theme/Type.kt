package mx.fer.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mx.fer.app.R

val OpenSans = FontFamily(
    Font(R.font.opensans_regular, FontWeight.Normal),
    Font(R.font.opensans_semibold, FontWeight.SemiBold),
    Font(R.font.opensans_bold, FontWeight.Bold)
)

val Typography = Typography(
    titleLarge = TextStyle(      // título del header: "Inicio", "Anuncios"
        fontFamily = OpenSans, fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(     // título de publicación
        fontFamily = OpenSans, fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(      // descripción
        fontFamily = OpenSans, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 17.sp
    ),
    labelLarge = TextStyle(      // tabs Becarios / Aspirantes (ajusta el tamaño si hace falta)
        fontFamily = OpenSans, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
    ),
    labelMedium = TextStyle(     // "Lo lo más reciente", títulos de sección
        fontFamily = OpenSans, fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(      // fecha y etiquetas de la barra inferior
        fontFamily = OpenSans, fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
)