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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hairup.R
import com.example.hairup.data.SessionManager
import com.example.hairup.ui.components.AppButton
import com.example.hairup.ui.components.AppTextInput
import com.example.hairup.ui.viewmodel.AuthViewModel
import com.example.hairup.ui.viewmodel.AuthViewModelFactory

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit, onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(sessionManager)
    )

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf("") }
    var showApiError by remember { mutableStateOf(false) }
    var apiErrorMessage by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.registerState) {
        viewModel.registerState.collect { state ->
            when (state) {
                is AuthViewModel.AuthState.Success -> {
                    onRegisterSuccess()
                }

                is AuthViewModel.AuthState.Error -> {
                    showApiError = true
                    apiErrorMessage = state.message
                }

                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        IconButton(
            onClick = onNavigateBack, modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color(0xFFD4AF37)
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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 2.dp, color = Color(0xFFD4AF37), shape = CircleShape
                    )
                    .padding(8.dp), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "HairUp Logo",
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape),
                    colorFilter = ColorFilter.tint(Color(0xFFD4AF37))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear Cuenta",
                fontSize = 28.sp,
                color = Color(0xFFD4AF37),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Únete a HairUp y comienza a reservar",
                fontSize = 14.sp,
                color = Color(0xFFB0B0B0),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            AppTextInput(
                value = name, onValueChange = {
                    name = it
                    validationError = ""
                    showApiError = false
                }, label = "Nombre completo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextInput(
                value = email,
                onValueChange = {
                    email = it
                    validationError = ""
                    showApiError = false
                },
                label = "Correo electrónico",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextInput(
                value = phone,
                onValueChange = {
                    phone = it
                    validationError = ""
                    showApiError = false
                },
                label = "Teléfono",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextInput(
                value = password,
                onValueChange = {
                    password = it
                    validationError = ""
                    showApiError = false
                },
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextInput(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    validationError = ""
                    showApiError = false
                },
                label = "Confirmar contraseña",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (validationError.isNotEmpty()) {
                Text(
                    text = validationError,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (showApiError) {
                Text(
                    text = apiErrorMessage,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (validationError.isNotEmpty() || showApiError) 16.dp else 24.dp))

            when (val state = viewModel.registerState.value) {
                is AuthViewModel.AuthState.Loading -> {
                    AppButton(
                        text = "CREANDO CUENTA...", onClick = {}, enabled = false
                    )
                }

                else -> {
                    AppButton(
                        text = "CREAR CUENTA",
                        onClick = {
                            when {
                                name.isBlank() -> validationError = "Por favor ingresa tu nombre"
                                email.isBlank() -> validationError = "Por favor ingresa tu correo"
                                !email.contains("@") -> validationError = "Correo inválido"
                                phone.isBlank() -> validationError = "Por favor ingresa tu teléfono"
                                password.isBlank() -> validationError =
                                    "Por favor ingresa una contraseña"

                                password.length < 6 -> validationError =
                                    "La contraseña debe tener al menos 6 caracteres"

                                password != confirmPassword -> validationError =
                                    "Las contraseñas no coinciden"

                                else -> {
                                    viewModel.register(email, password, name, phone)
                                }
                            }
                        },
                        enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Al registrarte, aceptas nuestros términos\ny condiciones de uso",
                fontSize = 12.sp,
                color = Color(0xFFB0B0B0),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}