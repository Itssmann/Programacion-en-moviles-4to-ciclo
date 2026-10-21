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

import com.abad.huellitasvet.ui.dueno.SplashScreen
import com.abad.huellitasvet.ui.dueno.LoginScreen
import com.abad.huellitasvet.ui.dueno.InicioScreen
import com.abad.huellitasvet.ui.dueno.MascotasScreen
import com.abad.huellitasvet.ui.dueno.FormularioMascotaScreen
import com.abad.huellitasvet.ui.dueno.FichaMascotaScreen
import com.abad.huellitasvet.ui.dueno.PedirCitaScreen
import com.abad.huellitasvet.ui.dueno.MisCitasScreen

import com.abad.huellitasvet.viewmodel.SesionViewModel
import com.abad.huellitasvet.viewmodel.InicioViewModel
import com.abad.huellitasvet.viewmodel.MascotaViewModel
import com.abad.huellitasvet.viewmodel.PedirCitaViewModel
import com.abad.huellitasvet.viewmodel.MisCitasViewModel

@Composable
fun NavVet(
    repository: VetRepository,
    preferencias: PreferenciasStore
) {
    val navController = rememberNavController()

    // SESIÓN
    val sesionFactory = remember(repository, preferencias) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T = SesionViewModel(repository, preferencias) as T
        }
    }

    val sesionViewModel: SesionViewModel = viewModel(
        factory = sesionFactory
    )

    val usuario by sesionViewModel.usuarioActual.collectAsState()
    val duenoId by sesionViewModel.usuarioId.collectAsState()

    // INICIO
    val inicioFactory = remember(repository, duenoId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T = InicioViewModel(repository, duenoId) as T
        }
    }

    val inicioViewModel: InicioViewModel = viewModel(
        key = "inicio_$duenoId",
        factory = inicioFactory
    )

    // MASCOTAS
    val mascotaFactory = remember(repository, duenoId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T = MascotaViewModel(repository, duenoId) as T
        }
    }

    val mascotaViewModel: MascotaViewModel = viewModel(
        key = "mascota_$duenoId",
        factory = mascotaFactory
    )

    // PEDIR CITA
    val pedirCitaFactory = remember(repository, duenoId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T = PedirCitaViewModel(repository, duenoId) as T
        }
    }

    val pedirCitaViewModel: PedirCitaViewModel = viewModel(
        key = "pedir_cita_$duenoId",
        factory = pedirCitaFactory
    )

    // MIS CITAS
    val misCitasFactory = remember(repository, duenoId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T = MisCitasViewModel(repository, duenoId) as T
        }
    }

    val misCitasViewModel: MisCitasViewModel = viewModel(
        key = "mis_citas_$duenoId",
        factory = misCitasFactory
    )

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {

        // 1. SPLASH
        composable(Rutas.SPLASH) {
            SplashScreen(
                preferencias = preferencias,
                onDestino = { destino ->
                    navController.navigate(destino) {
                        popUpTo(Rutas.SPLASH) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 2. LOGIN
        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = sesionViewModel,
                onLoginExitoso = { rol ->
                    val destino = if (rol == "ADMIN") {
                        Rutas.PANEL
                    } else {
                        Rutas.INICIO
                    }

                    navController.navigate(destino) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 3. INICIO
        composable(Rutas.INICIO) {
            InicioScreen(
                nombreDueno = usuario?.nombre ?: "Bienvenido",
                viewModel = inicioViewModel,
                onMascotas = {
                    navController.navigate(Rutas.MASCOTAS)
                },
                onAgregar = {
                    navController.navigate(Rutas.FORMULARIO_MASCOTA)
                },
                onPedirCita = {
                    navController.navigate(Rutas.PEDIR_CITA)
                },
                onMisCitas = {
                    navController.navigate(Rutas.MIS_CITAS)
                },
                onPerfil = {
                    navController.navigate(Rutas.PERFIL)
                }
            )
        }

        // 4. MASCOTAS
        composable(Rutas.MASCOTAS) {
            MascotasScreen(
                viewModel = inicioViewModel,
                onVolver = { navController.popBackStack() },
                onAgregar = {
                    navController.navigate(Rutas.FORMULARIO_MASCOTA)
                },
                onAbrirMascota = { id ->
                    navController.navigate(Rutas.fichaMascota(id))
                }
            )
        }

        // 5. REGISTRAR MASCOTA
        composable(Rutas.FORMULARIO_MASCOTA) {
            FormularioMascotaScreen(
                viewModel = mascotaViewModel,
                duenoId = duenoId,
                onVolver = { navController.popBackStack() }
            )
        }

        // 6. EDITAR MASCOTA
        composable("formulario_mascota/{mascotaId}") { entry ->
            val id = entry.arguments
                ?.getString("mascotaId")
                ?.toIntOrNull()

            FormularioMascotaScreen(
                viewModel = mascotaViewModel,
                duenoId = duenoId,
                mascotaId = id,
                onVolver = { navController.popBackStack() }
            )
        }

        // 7. FICHA E HISTORIAL CLÍNICO
        composable(Rutas.FICHA_MASCOTA) { entry ->
            val id = entry.arguments
                ?.getString("mascotaId")
                ?.toIntOrNull() ?: 0

            FichaMascotaScreen(
                mascotaId = id,
                viewModel = mascotaViewModel,
                onVolver = { navController.popBackStack() },
                onEditar = {
                    navController.navigate("formulario_mascota/$id")
                },
                onPedirCita = {
                    navController.navigate(Rutas.PEDIR_CITA)
                }
            )
        }

        // 8. PEDIR CITA
        composable(Rutas.PEDIR_CITA) {
            PedirCitaScreen(
                viewModel = pedirCitaViewModel,
                onVolver = { navController.popBackStack() },
                onConfirmada = {
                    navController.navigate(Rutas.MIS_CITAS) {
                        popUpTo(Rutas.PEDIR_CITA) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 9. MIS CITAS
        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(
                viewModel = misCitasViewModel,
                onVolver = { navController.popBackStack() },
                onPedirCita = {
                    navController.navigate(Rutas.PEDIR_CITA)
                }
            )
        }

        // 10. PANEL VETERINARIO TEMPORAL
        composable(Rutas.PANEL) {
            PantallaTemporal(
                titulo = "Panel veterinario",
                descripcion = "Próximamente: agenda y estadísticas",
                onCerrarSesion = {
                    sesionViewModel.cerrarSesion()

                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.PANEL) {
                            inclusive = true
                        }
                        launchSingleTop = true
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
                titulo,
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
