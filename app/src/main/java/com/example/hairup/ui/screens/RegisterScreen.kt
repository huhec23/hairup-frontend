package com.example.hairup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.R
import com.example.hairup.ui.components.AppButton
import com.example.hairup.ui.components.AppTextInput
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Carbon Black
    ) {
        // Botón de volver
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color(0xFFD4AF37) // Gold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo pequeño
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFD4AF37), // Gold
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "HairUp Logo",
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape),
                    colorFilter = ColorFilter.tint(Color(0xFFD4AF37)) // Gold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear Cuenta",
                fontSize = 28.sp,
                color = Color(0xFFD4AF37), // Gold
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Únete a HairUp y comienza a reservar",
                fontSize = 14.sp,
                color = Color(0xFFB0B0B0), // TextGray
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de nombre
            AppTextInput(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = ""
                },
                label = "Nombre completo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de email
            AppTextInput(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = "Correo electrónico",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            AppTextInput(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de confirmar contraseña
            AppTextInput(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = "Confirmar contraseña",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje de error
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B), // Red
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de registro
            AppButton(
                text = "CREAR CUENTA",
                onClick = {
                    // Validaciones
                    when {
                        name.isBlank() -> errorMessage = "Por favor ingresa tu nombre"
                        email.isBlank() -> errorMessage = "Por favor ingresa tu correo"
                        !email.contains("@") -> errorMessage = "Correo inválido"
                        password.isBlank() -> errorMessage = "Por favor ingresa una contraseña"
                        password.length < 6 -> errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        password != confirmPassword -> errorMessage = "Las contraseñas no coinciden"
                        else -> {
                            // Registro exitoso
                            onRegisterSuccess()
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Al registrarte, aceptas nuestros términos\ny condiciones de uso",
                fontSize = 12.sp,
                color = Color(0xFFB0B0B0), // TextGray
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
