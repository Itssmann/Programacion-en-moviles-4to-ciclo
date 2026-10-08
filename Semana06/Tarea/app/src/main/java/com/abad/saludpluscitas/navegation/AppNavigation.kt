package com.abad.saludpluscitas.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abad.saludpluscitas.data.model.Cita
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraInferior
import com.abad.saludpluscitas.ui.screens.auth.SplashScreen
import com.abad.saludpluscitas.ui.screens.auth.LoginScreen
import com.abad.saludpluscitas.ui.screens.auth.RegistroScreen
import com.abad.saludpluscitas.ui.screens.auth.TerminosScreen
import com.abad.saludpluscitas.ui.screens.home.HomeScreen
import com.abad.saludpluscitas.ui.screens.agendamiento.EspecialidadesScreen
import com.abad.saludpluscitas.ui.screens.agendamiento.MedicosScreen
import com.abad.saludpluscitas.ui.screens.agendamiento.FechaHoraScreen
import com.abad.saludpluscitas.ui.screens.agendamiento.ConfirmarCitaScreen
import com.abad.saludpluscitas.ui.screens.agendamiento.CitaExitosaScreen
import com.abad.saludpluscitas.ui.screens.citas.MisCitasScreen
import com.abad.saludpluscitas.ui.screens.citas.DetalleCitaScreen
import com.abad.saludpluscitas.ui.screens.perfil.PerfilScreen
import com.abad.saludpluscitas.ui.screens.resultados.ResultadosScreen
import com.abad.saludpluscitas.ui.screens.notificaciones.NotificacionesScreen

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
                        if (ruta != rutaActual) {
                            if (ruta == Rutas.Inicio.ruta) {
                                navController.popBackStack(
                                    Rutas.Inicio.ruta,
                                    false
                                )
                            } else {
                                navController.navigate(ruta) {
                                    popUpTo(Rutas.Inicio.ruta) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
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
                            launchSingleTop = true
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

                FechaHoraScreen(
                    medicoId = medicoId,
                    onContinuar = { fecha, hora ->
                        navController.navigate(
                            "confirmar/$medicoId/$fecha/$hora"
                        )
                    },
                    onVolver = {
                        navController.popBackStack()
                    }
                )
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
                ConfirmarCitaScreen(
                    medicoId = medicoId,
                    fecha = fecha,
                    hora = hora,
                    onConfirmar = {
                        val nuevaCita = Cita(
                            id = (Repositorio.citas.maxOfOrNull { it.id } ?: 0) + 1,
                            usuarioId = 1,
                            medicoId = medicoId,
                            fecha = fecha,
                            hora = hora,
                            estado = "Confirmada"
                        )
                        Repositorio.registrarCita(nuevaCita)
                        navController.navigate(Rutas.CitaExitosa.ruta) {
                            popUpTo(Rutas.Inicio.ruta)
                            launchSingleTop = true
                        }
                    },
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Rutas.CitaExitosa.ruta) {
                CitaExitosaScreen(
                    onVerCitas = {
                        navController.navigate(Rutas.MisCitas.ruta) {
                            popUpTo(Rutas.Inicio.ruta)
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Rutas.MisCitas.ruta) {
                MisCitasScreen(
                    onSeleccionarCita = { citaId ->
                        navController.navigate("detalleCita/$citaId")
                    }
                )
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
                DetalleCitaScreen(
                    citaId = citaId,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Rutas.Perfil.ruta) {
                PerfilScreen()
            }
            composable(Rutas.Resultados.ruta) {
                ResultadosScreen()
            }
            composable(Rutas.Notificaciones.ruta) {
                NotificacionesScreen(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
