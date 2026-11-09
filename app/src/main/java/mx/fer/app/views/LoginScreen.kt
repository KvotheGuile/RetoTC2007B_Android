package mx.fer.app.views

import mx.fer.app.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.fer.app.model.UserType

private val Azul = Color(0xFF1B3A87)
private val Fondo = Color(0xFFF2F2F2)
@Composable
fun LoginScreen(
    onContinuar: (String, UserType) -> Unit
) {
    // rememberSaveable keeps the input across rotation
    var nombre by rememberSaveable { mutableStateOf("") }
    var rol by rememberSaveable { mutableStateOf(UserType.ASPIRANTE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_fer),
            contentDescription = "Logo Fundacion Esposo Rodriguez",
            modifier = Modifier
                .size(170.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.height(56.dp))

        Text(
            text = "¡Bienvenido!",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(56.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = { Text("Tu nombre...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Azul,
                unfocusedBorderColor = Azul
            )
        )

        Spacer(Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserType.entries.forEach { type ->
                val selected = rol == type
                Button(
                    onClick = { rol = type },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(2.dp, Azul),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) Azul else Color.White,
                        contentColor = if (selected) Color.White else Azul
                    )
                ) {
                    Text(type.label, fontSize = 20.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onContinuar(nombre.trim(), rol) },
            enabled = nombre.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            border = BorderStroke(1.dp, Azul)
        ) {
            Text(text = "Continuar", color = Azul)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onContinuar = { _, _ -> })
}