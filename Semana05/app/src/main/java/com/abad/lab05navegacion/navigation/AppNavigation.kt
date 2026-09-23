package com.abad.lab05navegacion.navigation

import android.R.attr.type
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            // TODO: llamar a HomeScreen(navController)
        }
        composable(Screen.List.route) {
            // TODO: llamar a ListScreen(navController)
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            // TODO: llamar a DetailScreen(itemId, navController)
        }
        composable(Screen.Profile.route) {
            // TODO: llamar a ProfileScreen(navController)
        }
    }
}