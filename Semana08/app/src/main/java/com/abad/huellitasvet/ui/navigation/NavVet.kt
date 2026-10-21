package com.abad.huellitasvet.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import com.abad.huellitasvet.data.repository.VetRepository
import com.abad.huellitasvet.ui.dueno.LoginScreen
import com.abad.huellitasvet.ui.dueno.SplashScreen
import com.abad.huellitasvet.viewmodel.SesionViewModel

@Composable
fun NavVet(
    repository: VetRepository,
    preferencias: PreferenciasStore
) {
    val navController = rememberNavController()

    val factory = remember(repository, preferencias) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SesionViewModel(repository, preferencias) as T
            }
        }
    }

    val sesionViewModel: SesionViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {
        composable(Rutas.SPLASH) {
            SplashScreen(preferencias) { destino ->
                navController.navigate(destino) {
                    popUpTo(Rutas.SPLASH) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(Rutas.LOGIN) {
            LoginScreen(sesionViewModel) { rol ->
                val destino = if (rol == "ADMIN") {
                    Rutas.PANEL
                } else {
                    Rutas.INICIO
                }

                navController.navigate(destino) {
                    popUpTo(Rutas.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(Rutas.INICIO) {
            PantallaTemporal(
                titulo = "Inicio del dueño",
                descripcion = "Próximamente: mascotas y próximas citas",
                onCerrarSesion = {
                    sesionViewModel.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.INICIO) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.PANEL) {
            PantallaTemporal(
                titulo = "Panel veterinario",
                descripcion = "Próximamente: agenda y estadísticas",
                onCerrarSesion = {
                    sesionViewModel.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.PANEL) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun PantallaTemporal(
    titulo: String,
    descripcion: String,
    onCerrarSesion: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(12.dp))

            Text(descripcion)

            Spacer(Modifier.height(24.dp))

            Button(onClick = onCerrarSesion) {
                Text("Cerrar sesión")
            }
        }
    }
}

