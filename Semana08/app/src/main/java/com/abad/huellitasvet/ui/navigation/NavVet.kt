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
import com.abad.huellitasvet.ui.dueno.*
import com.abad.huellitasvet.ui.veterinario.PanelScreen
import com.abad.huellitasvet.ui.veterinario.AgendaScreen
import com.abad.huellitasvet.viewmodel.*

@Composable
fun NavVet(
    repository: VetRepository,
    preferencias: PreferenciasStore
) {
    val navController = rememberNavController()

    val sesionFactory = remember(repository, preferencias) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SesionViewModel(repository, preferencias) as T
        }
    }
    val sesionViewModel: SesionViewModel = viewModel(
        factory = sesionFactory
    )

    val usuario by sesionViewModel.usuarioActual.collectAsState()
    val duenoId by sesionViewModel.usuarioId.collectAsState()

    val inicioFactory = remember(repository, duenoId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                InicioViewModel(repository, duenoId) as T
        }
    }
    val inicioViewModel: InicioViewModel = viewModel(
        key = "inicio_$duenoId",
        factory = inicioFactory
    )

    val mascotaFactory = remember(repository, duenoId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MascotaViewModel(repository, duenoId) as T
        }
    }
    val mascotaViewModel: MascotaViewModel = viewModel(
        key = "mascota_$duenoId",
        factory = mascotaFactory
    )

    val pedirCitaFactory = remember(repository, duenoId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                PedirCitaViewModel(repository, duenoId) as T
        }
    }
    val pedirCitaViewModel: PedirCitaViewModel = viewModel(
        key = "pedir_cita_$duenoId",
        factory = pedirCitaFactory
    )

    val misCitasFactory = remember(repository, duenoId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MisCitasViewModel(repository, duenoId) as T
        }
    }
    val misCitasViewModel: MisCitasViewModel = viewModel(
        key = "mis_citas_$duenoId",
        factory = misCitasFactory
    )

    val panelFactory = remember(repository) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                PanelViewModel(repository) as T
        }
    }
    val panelViewModel: PanelViewModel = viewModel(
        factory = panelFactory
    )

    val agendaFactory = remember(repository) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AgendaViewModel(repository) as T
        }
    }
    val agendaViewModel: AgendaViewModel = viewModel(
        factory = agendaFactory
    )

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {
        composable(Rutas.SPLASH) {
            SplashScreen(
                preferencias = preferencias,
                onDestino = { destino ->
                    navController.navigate(destino) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = sesionViewModel,
                onLoginExitoso = { rol ->
                    navController.navigate(
                        if (rol == "ADMIN") Rutas.PANEL else Rutas.INICIO
                    ) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Rutas.INICIO) {
            InicioScreen(
                nombreDueno = usuario?.nombre ?: "Bienvenido",
                viewModel = inicioViewModel,
                onMascotas = { navController.navigate(Rutas.MASCOTAS) },
                onAgregar = {
                    navController.navigate(Rutas.FORMULARIO_MASCOTA)
                },
                onPedirCita = { navController.navigate(Rutas.PEDIR_CITA) },
                onMisCitas = { navController.navigate(Rutas.MIS_CITAS) },
                onPerfil = { navController.navigate(Rutas.PERFIL) }
            )
        }

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

        composable(Rutas.FORMULARIO_MASCOTA) {
            FormularioMascotaScreen(
                viewModel = mascotaViewModel,
                duenoId = duenoId,
                onVolver = { navController.popBackStack() }
            )
        }

        composable("formulario_mascota/{mascotaId}") { entry ->
            val id = entry.arguments
                ?.getString("mascotaId")?.toIntOrNull()

            FormularioMascotaScreen(
                viewModel = mascotaViewModel,
                duenoId = duenoId,
                mascotaId = id,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.FICHA_MASCOTA) { entry ->
            val id = entry.arguments
                ?.getString("mascotaId")?.toIntOrNull() ?: 0

            FichaMascotaScreen(
                mascotaId = id,
                viewModel = mascotaViewModel,
                onVolver = { navController.popBackStack() },
                onEditar = {
                    navController.navigate("formulario_mascota/$id")
                },
                onPedirCita = { navController.navigate(Rutas.PEDIR_CITA) }
            )
        }

        composable(Rutas.PEDIR_CITA) {
            PedirCitaScreen(
                viewModel = pedirCitaViewModel,
                onVolver = { navController.popBackStack() },
                onConfirmada = {
                    navController.navigate(Rutas.MIS_CITAS) {
                        popUpTo(Rutas.PEDIR_CITA) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(
                viewModel = misCitasViewModel,
                onVolver = { navController.popBackStack() },
                onPedirCita = { navController.navigate(Rutas.PEDIR_CITA) }
            )
        }

        // PANEL VETERINARIO
        composable(Rutas.PANEL) {
            PanelScreen(
                viewModel = panelViewModel,
                onAgenda = { navController.navigate(Rutas.AGENDA) },
                onCerrarSesion = {
                    sesionViewModel.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.PANEL) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // AGENDA VETERINARIA
        composable(Rutas.AGENDA) {
            AgendaScreen(
                viewModel = agendaViewModel,
                onVolver = { navController.popBackStack() },
                onAtender = { id ->
                    navController.navigate(Rutas.atenderCita(id))
                }
            )
        }

        // PANTALLA TEMPORAL HASTA EL COMMIT 13
        composable(Rutas.ATENDER_CITA) {
            Scaffold { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Atención de cita",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text("Se implementará en el commit 13")
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}
