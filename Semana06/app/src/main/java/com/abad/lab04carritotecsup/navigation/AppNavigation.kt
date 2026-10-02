package com.abad.lab04carritotecsup.navigation

import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abad.lab04carritotecsup.drawer.AppDrawer
import com.abad.lab04carritotecsup.screens.FavoritosScreen
import com.abad.lab04carritotecsup.screens.HomeScreen
import com.abad.lab04carritotecsup.screens.PedidosScreen
import com.abad.lab04carritotecsup.screens.ProfileScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    ModalNavigationDrawer(
        drawerContent = {
            AppDrawer()
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Pedidos.route) {
                PedidosScreen()
            }

            composable(Screen.Favoritos.route) {
                FavoritosScreen()
            }

            composable(Screen.Perfil.route) {
                ProfileScreen()
            }
        }
    }
}