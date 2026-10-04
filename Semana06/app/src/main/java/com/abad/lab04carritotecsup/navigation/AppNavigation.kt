package com.abad.lab04carritotecsup.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abad.lab04carritotecsup.Producto
import com.abad.lab04carritotecsup.drawer.AppDrawer
import com.abad.lab04carritotecsup.screens.FavoritosScreen
import com.abad.lab04carritotecsup.screens.HomeScreen
import com.abad.lab04carritotecsup.screens.PedidosScreen
import com.abad.lab04carritotecsup.screens.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val favoritos = remember { mutableStateListOf<Producto>() }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: Screen.Home.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                rutaActual = rutaActual,
                favoritoCount = favoritos.size,
                onNavigate = { ruta ->
                    navController.navigate(ruta) {
                        launchSingleTop = true
                    }
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("TECSUP Store") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onAgregarFavorito = { producto ->
                            if (!favoritos.contains(producto)) {
                                favoritos.add(producto)
                            }
                        }
                    )
                }

                composable(Screen.Pedidos.route) {
                    PedidosScreen()
                }

                composable(Screen.Favoritos.route) {
                    FavoritosScreen(
                        favoritos = favoritos,
                        onEliminarFavorito = { producto ->
                            favoritos.remove(producto)
                        }
                    )
                }

                composable(Screen.Perfil.route) {
                    ProfileScreen()
                }
            }
        }
    }
}