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
    headlineMedium = TextStyle( // títulos de pantalla: "Inicio", "Anuncios"
        fontFamily = OpenSans, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp
    ),
    titleMedium = TextStyle( // título de publicación
        fontFamily = OpenSans, fontWeight = FontWeight.Bold,
        fontSize = 17.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle( // descripciones
        fontFamily = OpenSans, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 21.sp
    ),
    labelMedium = TextStyle( // fechas, etiquetas de la barra inferior
        fontFamily = OpenSans, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp
    )
)