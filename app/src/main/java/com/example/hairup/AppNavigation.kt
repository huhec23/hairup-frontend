package com.example.hairup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hairup.data.SessionManager
import com.example.hairup.ui.screens.ClientHomeScreen
import com.example.hairup.ui.screens.LoginScreen
import com.example.hairup.ui.screens.RegisterScreen
import com.example.hairup.ui.screens.admin.AdminHomeScreen
import com.example.hairup.ui.screens.client.BookingScreen
import com.example.hairup.ui.screens.client.LoyaltyScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = { isAdmin ->
                if (isAdmin) {
                    navController.navigate("admin_home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("client_home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }, onNavigateToRegister = {
                navController.navigate("register")
            })
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = {
                navController.navigate("client_home") {
                    popUpTo("login") { inclusive = true }
                }
            }, onNavigateBack = {
                navController.popBackStack()
            })
        }

        composable("client_home") {
            ClientHomeScreen(
                onNavigateToBooking = { navController.navigate("booking") },
                onNavigateToLoyalty = { navController.navigate("client/loyalty") },
                onLogout = {
                    sessionManager.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                })
        }

        composable("client/loyalty") {
            LoyaltyScreen(
                onBack = { navController.popBackStack() })
        }

        composable("booking") {
            BookingScreen(
                onBookingComplete = { navController.popBackStack() },
                onBack = { navController.popBackStack() })
        }

        composable("admin_home") {
            AdminHomeScreen()
        }
    }
}