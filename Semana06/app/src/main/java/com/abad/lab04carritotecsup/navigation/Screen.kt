package com.abad.lab04carritotecsup.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Pedidos : Screen("pedidos")
    object Favoritos : Screen("favoritos")
    object Perfil : Screen("perfil")
}