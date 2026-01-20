package com.example.hairup

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text
import com.example.hairup.ui.screens.LoginScreen
import com.example.hairup.ui.screens.ClientHomeScreen
import com.example.hairup.ui.screens.client.BookingScreen
import com.example.hairup.ui.screens.admin.AdminHomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate("admin_home")
                    } else {
                        navController.navigate("client_home")
                    }
                }
            )
        }

        composable("client_home") {
            ClientHomeScreen(
                onNavigateToBooking = { navController.navigate("booking") }
            )
        }

        composable("booking") {
            BookingScreen(
                onBookingComplete = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable("admin_home") {
            AdminHomeScreen()
        }
    }
}