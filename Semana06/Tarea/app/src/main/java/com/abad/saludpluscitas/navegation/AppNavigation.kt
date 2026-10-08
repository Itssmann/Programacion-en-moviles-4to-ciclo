package com.abad.saludpluscitas.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abad.saludpluscitas.ui.screens.auth.SplashScreen
import com.abad.saludpluscitas.ui.screens.auth.LoginScreen
import com.abad.saludpluscitas.ui.screens.auth.RegistroScreen
import com.abad.saludpluscitas.ui.screens.auth.TerminosScreen
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.Splash.ruta
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
            PantallaTemporal("Inicio") {
                navController.navigate(Rutas.Especialidades.ruta)
            }
        }
        composable(Rutas.Especialidades.ruta) {
            PantallaTemporal("Especialidades") {
                navController.navigate("medicos/1")
            }
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
            PantallaTemporal("Medicos de especialidad $especialidadId") {
                navController.navigate("fechaHora/1")
            }
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