
package com.abad.saludpluscitas.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abad.saludpluscitas.ui.components.BarraInferior
import com.abad.saludpluscitas.ui.screens.auth.SplashScreen
import com.abad.saludpluscitas.ui.screens.auth.LoginScreen
import com.abad.saludpluscitas.ui.screens.auth.RegistroScreen
import com.abad.saludpluscitas.ui.screens.auth.TerminosScreen
import com.abad.saludpluscitas.ui.screens.home.HomeScreen
import com.abad.saludpluscitas.ui.screens.agendamiento.EspecialidadesScreen
import com.abad.saludpluscitas.ui.screens.agendamiento.MedicosScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: ""
    val mostrarBarra = rutaActual in listOf(
        Rutas.Inicio.ruta,
        Rutas.MisCitas.ruta,
        Rutas.Resultados.ruta,
        Rutas.Perfil.ruta
    )
    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                BarraInferior(
                    rutaActual = rutaActual,
                    onNavegar = { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(Rutas.Inicio.ruta) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Rutas.Splash.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Rutas.Splash.ruta) {
                SplashScreen(
                    onContinuar = {
                        navController.navigate(Rutas.Login.ruta)
                    }
                )
            }
            composable(Rutas.Login.ruta) {
                LoginScreen(
                    onIngresar = {
                        navController.navigate(Rutas.Inicio.ruta) {
                            popUpTo(Rutas.Login.ruta) {
                                inclusive = true
                            }
                        }
                    },
                    onRegistro = {
                        navController.navigate(Rutas.Registro.ruta)
                    }
                )
            }
            composable(Rutas.Registro.ruta) {
                RegistroScreen(
                    onRegistroExitoso = {
                        navController.navigate(Rutas.Login.ruta) {
                            popUpTo(Rutas.Registro.ruta) {
                                inclusive = true
                            }
                        }
                    },
                    onTerminos = {
                        navController.navigate(Rutas.Terminos.ruta)
                    }
                )
            }
            composable(Rutas.Terminos.ruta) {
                TerminosScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Rutas.Inicio.ruta) {
                HomeScreen(
                    onEspecialidades = {
                        navController.navigate(Rutas.Especialidades.ruta)
                    },
                    onSeleccionarEspecialidad = { especialidadId ->
                        navController.navigate("medicos/$especialidadId")
                    }
                )
            }
            composable(Rutas.Especialidades.ruta) {
                EspecialidadesScreen(
                    onSeleccionarEspecialidad = { especialidadId ->
                        navController.navigate("medicos/$especialidadId")
                    },
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Rutas.Medicos.ruta,
                arguments = listOf(
                    navArgument("especialidadId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val especialidadId =
                    backStackEntry.arguments?.getInt("especialidadId") ?: 0
                MedicosScreen(
                    especialidadId = especialidadId,
                    onSeleccionarMedico = { medicoId ->
                        navController.navigate("fechaHora/$medicoId")
                    },
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Rutas.FechaHora.ruta,
                arguments = listOf(
                    navArgument("medicoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val medicoId =
                    backStackEntry.arguments?.getInt("medicoId") ?: 0
                PantallaTemporal("Fecha y hora - Medico $medicoId") {
                    navController.navigate("confirmar/$medicoId/2026-10-15/10:00")
                }
            }
            composable(
                route = Rutas.ConfirmarCita.ruta,
                arguments = listOf(
                    navArgument("medicoId") {
                        type = NavType.IntType
                    },
                    navArgument("fecha") {
                        type = NavType.StringType
                    },
                    navArgument("hora") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val medicoId =
                    backStackEntry.arguments?.getInt("medicoId") ?: 0
                val fecha =
                    backStackEntry.arguments?.getString("fecha") ?: ""
                val hora =
                    backStackEntry.arguments?.getString("hora") ?: ""
                PantallaTemporal("Confirmar cita: $medicoId - $fecha - $hora") {
                    navController.navigate(Rutas.CitaExitosa.ruta)
                }
            }
            composable(Rutas.CitaExitosa.ruta) {
                PantallaTemporal("Cita registrada correctamente") {
                    navController.navigate(Rutas.MisCitas.ruta)
                }
            }
            composable(Rutas.MisCitas.ruta) {
                PantallaTemporal("Mis citas") {
                    navController.navigate("detalleCita/1")
                }
            }
            composable(
                route = Rutas.DetalleCita.ruta,
                arguments = listOf(
                    navArgument("citaId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val citaId =
                    backStackEntry.arguments?.getInt("citaId") ?: 0
                PantallaTemporal("Detalle de cita $citaId") {
                    navController.popBackStack()
                }
            }
            composable(Rutas.Perfil.ruta) {
                PantallaTemporal("Mi perfil") {
                    navController.navigate(Rutas.Inicio.ruta)
                }
            }
            composable(Rutas.Resultados.ruta) {
                PantallaTemporal("Resultados medicos") {
                    navController.navigate(Rutas.Inicio.ruta)
                }
            }
            composable(Rutas.Notificaciones.ruta) {
                PantallaTemporal("Notificaciones") {
                    navController.navigate(Rutas.Inicio.ruta)
                }
            }
        }
    }
}
@Composable
fun PantallaTemporal(
    titulo: String,
    onContinuar: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = titulo)

        Button(onClick = onContinuar) {
            Text("Continuar")
        }
    }
}
