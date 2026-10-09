package com.abad.saludpluscitas.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abad.saludpluscitas.data.model.Cita
import com.abad.saludpluscitas.data.repository.InfoCitaRegistrada
import com.abad.saludpluscitas.data.repository.Repositorio
import com.abad.saludpluscitas.ui.components.BarraInferior
import com.abad.saludpluscitas.ui.screens.auth.SplashScreen
import com.abad.saludpluscitas.ui.screens.auth.LoginScreen
import com.abad.saludpluscitas.ui.screens.auth.RegistroScreen
import com.abad.saludpluscitas.ui.screens.auth.TerminosScreen
import com.abad.saludpluscitas.ui.screens.home.HomeScreen
import com.abad.saludpluscitas.ui.screens.sedes.SedesScreen
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
import kotlinx.coroutines.launch

var medicoParaReservarPendiente: Int? = null

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: ""
    val mostrarBarra = rutaActual in listOf(
        Rutas.Inicio.ruta,
        Rutas.MisCitas.ruta,
        Rutas.Resultados.ruta,
        Rutas.Perfil.ruta
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Repositorio.usuarioActual?.nombre ?: "Paciente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Clínica SaludPlus",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                val itemsMenu = listOf(
                    Triple("Inicio", Rutas.Inicio.ruta, Icons.Default.Home),
                    Triple("Citas", Rutas.MisCitas.ruta, Icons.Default.CalendarMonth),
                    Triple("Resultados", Rutas.Resultados.ruta, Icons.Default.Description),
                    Triple("Perfil", Rutas.Perfil.ruta, Icons.Default.Person)
                )

                itemsMenu.forEach { (titulo, ruta, icono) ->
                    val seleccionado = rutaActual == ruta
                    NavigationDrawerItem(
                        icon = { Icon(imageVector = icono, contentDescription = titulo) },
                        label = { Text(titulo, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal) },
                        selected = seleccionado,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
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
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
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
                            navController.navigate(Rutas.Registro.ruta)
                        },
                        onLogin = {
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
                        },
                        onLogin = {
                            navController.navigate(Rutas.Login.ruta) {
                                popUpTo(Rutas.Registro.ruta) {
                                    inclusive = true
                                }
                            }
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
                        onAbrirMenu = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onNotificaciones = {
                            navController.navigate(Rutas.Notificaciones.ruta)
                        },
                        onSedes = {
                            Repositorio.sedeActual = null
                            medicoParaReservarPendiente = null
                            navController.navigate(Rutas.Sedes.ruta)
                        },
                        onDoctores = {
                            Repositorio.sedeActual = null
                            medicoParaReservarPendiente = null
                            navController.navigate(Rutas.Especialidades.ruta)
                        },
                        onEspecialidades = {
                            navController.navigate(Rutas.Especialidades.ruta)
                        },
                        onSeleccionarEspecialidad = { especialidadId ->
                            navController.navigate("medicos/$especialidadId")
                        },
                        onMisCitas = {
                            navController.navigate(Rutas.MisCitas.ruta)
                        },
                        onPerfil = {
                            navController.navigate(Rutas.Perfil.ruta)
                        },
                        onResultados = {
                            navController.navigate(Rutas.Resultados.ruta)
                        }
                    )
                }
                composable(Rutas.Sedes.ruta) {
                    SedesScreen(
                        onSedeSeleccionada = { sede ->
                            Repositorio.sedeActual = sede
                            val medicoPendiente = medicoParaReservarPendiente
                            medicoParaReservarPendiente = null
                            if (medicoPendiente != null) {
                                navController.navigate("fechaHora/$medicoPendiente") {
                                    popUpTo(Rutas.Inicio.ruta)
                                }
                            } else {
                                navController.navigate(Rutas.Especialidades.ruta)
                            }
                        },
                        onVolver = {
                            Repositorio.sedeActual = null
                            medicoParaReservarPendiente = null
                            navController.popBackStack()
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
                        onReservarSinSede = { medicoId ->
                            medicoParaReservarPendiente = medicoId
                            navController.navigate(Rutas.Sedes.ruta)
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
                            val usuarioActualId = Repositorio.usuarioActual?.id ?: 1
                            val sedeNombre = Repositorio.sedeActual?.let { "${it.nombre} - ${it.direccion}" } ?: "Sede Principal (Jesús María)"
                            if (!Repositorio.estaHorarioOcupado(medicoId, fecha, hora)) {
                                val medico = Repositorio.obtenerMedicoPorId(medicoId)
                                val especialidad = Repositorio.especialidades.find { it.id == medico?.especialidadId }?.nombre ?: "Medicina General"
                                val nuevaCita = Cita(
                                    id = (Repositorio.citas.maxOfOrNull { it.id } ?: 0) + 1,
                                    usuarioId = usuarioActualId,
                                    medicoId = medicoId,
                                    fecha = fecha,
                                    hora = hora,
                                    estado = "Confirmada",
                                    sede = sedeNombre
                                )
                                val registrado = Repositorio.registrarCita(nuevaCita)
                                if (registrado) {
                                    Repositorio.ultimaCitaRegistrada = InfoCitaRegistrada(
                                        medicoNombre = medico?.nombre ?: "",
                                        especialidad = especialidad,
                                        sede = sedeNombre,
                                        fecha = fecha,
                                        hora = hora
                                    )
                                    navController.navigate(Rutas.CitaExitosa.ruta) {
                                        popUpTo(Rutas.Inicio.ruta)
                                        launchSingleTop = true
                                    }
                                }
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
                    PerfilScreen(
                        onCerrarSesion = {
                            Repositorio.cerrarSesion()
                            navController.navigate(Rutas.Login.ruta) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
                composable(Rutas.Resultados.ruta) {
                    ResultadosScreen()
                }
                composable(Rutas.Notificaciones.ruta) {
                    NotificacionesScreen(
                        onSeleccionarCita = { citaId ->
                            navController.navigate("detalleCita/$citaId")
                        },
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
