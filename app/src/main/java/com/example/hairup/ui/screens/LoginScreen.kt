package com.example.hairup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun LoginScreen(
    onLoginSuccess: (Boolean) -> Unit, onNavigateToRegister: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(sessionManager)
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.loginState) {
        viewModel.loginState.collect { state ->
            when (state) {
                is AuthViewModel.AuthState.Success -> {
                    onLoginSuccess(state.isAdmin)
                }

                is AuthViewModel.AuthState.Error -> {
                    showError = true
                    errorMessage = state.message
                }

                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Carbon Black
    ) {
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
                    .size(200.dp)
                    .border(
                        width = 3.dp, color = Color(0xFFD4AF37),
                        shape = CircleShape
                    )
                    .padding(12.dp), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "HairUp Logo",
                    modifier = Modifier
                        .size(176.dp)
                        .clip(CircleShape),
                    colorFilter = ColorFilter.tint(Color(0xFFD4AF37))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            DecorativeLine()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tu app para reservas en peluquerías",
                fontSize = 16.sp,
                color = Color(0xFFB0B0B0), // TextGray
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            DecorativeLine()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Bienvenido de nuevo",
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF).copy(alpha = 0.9f),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(40.dp))

            AppTextInput(
                value = email, onValueChange = {
                    email = it
                    showError = false
                }, label = "Correo electrónico"
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppTextInput(
                value = password, onValueChange = {
                    password = it
                    showError = false
                }, label = "Contraseña", visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (showError) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (showError) 16.dp else 40.dp))

            when (val state = viewModel.loginState.value) {
                is AuthViewModel.AuthState.Loading -> {
                    AppButton(
                        text = "INICIANDO SESIÓN...", onClick = {}, enabled = false
                    )
                }

                else -> {
                    AppButton(
                        text = "INICIAR SESIÓN", onClick = {
                            viewModel.login(email, password)
                        }, enabled = email.isNotBlank() && password.isNotBlank()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "¿No tienes cuenta? ", color = Color(0xFFB0B0B0), fontSize = 14.sp
                )
                Text(
                    text = "Regístrate", color = Color(0xFFD4AF37),
                    fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DecorativeLine() {
    Row(
        modifier = Modifier.fillMaxWidth(0.6f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFD4AF37)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFFD4AF37))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Divider(
            modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFD4AF37)
        )
    }
}