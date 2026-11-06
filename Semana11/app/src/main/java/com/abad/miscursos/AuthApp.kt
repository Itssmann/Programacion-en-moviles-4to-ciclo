package com.abad.miscursos

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthApp() {
    val navController = rememberNavController()
    val inicio = if (FirebaseAuth.getInstance().currentUser != null)
        "home" else "login"

    NavHost(navController, startDestination = inicio) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
