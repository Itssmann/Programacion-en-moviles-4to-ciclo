package com.abad.lab05navegacion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abad.lab05navegacion.screens.DetailScreen
import com.abad.lab05navegacion.screens.ForgotPasswordScreen
import com.abad.lab05navegacion.screens.HomeScreen
import com.abad.lab05navegacion.screens.ListScreen
import com.abad.lab05navegacion.screens.LoginScreen
import com.abad.lab05navegacion.screens.ProfileScreen
import com.abad.lab05navegacion.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.List.route) {
            ListScreen(navController)
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            DetailScreen(navController, itemId)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
