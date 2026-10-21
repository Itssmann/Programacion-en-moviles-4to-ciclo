package com.abad.huellitasvet.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import com.abad.huellitasvet.data.repository.VetRepository
import com.abad.huellitasvet.ui.dueno.*
import com.abad.huellitasvet.ui.veterinario.*
import com.abad.huellitasvet.viewmodel.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

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
    val sesionViewModel: SesionViewModel = viewModel(factory = sesionFactory)

    val usuario by sesionViewModel.usuarioActual.collectAsState()
    val duenoId by sesionViewModel.usuarioId.collectAsState()
    val modoOscuro by sesionViewModel.modoOscuro.collectAsState()

    val notificacionesGuardadas by preferencias.notificaciones.collectAsState(
        initial = true
    )
    val aceptarCitasGuardado by preferencias.aceptarCitas.collectAsState(
        initial = true
    )

    val scope = rememberCoroutineScope()

    val notificaciones = notificacionesGuardadas
    val aceptarCitas = aceptarCitasGuardado

    val cambiarNotificaciones: (Boolean) -> Unit = { valor ->
        scope.launch {
            preferencias.guardarNotificaciones(valor)
        }
    }

    val cambiarAceptarCitas: (Boolean) -> Unit = { valor ->
        scope.launch {
            preferencias.guardarAceptarCitas(valor)
        }
    }

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
    val panelViewModel: PanelViewModel = viewModel(factory = panelFactory)

    val agendaFactory = remember(repository) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AgendaViewModel(repository) as T
        }
    }
    val agendaViewModel: AgendaViewModel = viewModel(factory = agendaFactory)

    val pacientesFactory = remember(repository) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                PacientesViewModel(repository) as T
        }
    }
    val pacientesViewModel: PacientesViewModel = viewModel(
        factory = pacientesFactory
    )

    val serviciosFactory = remember(repository) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ServiciosViewModel(repository) as T
        }
    }
    val serviciosViewModel: ServiciosViewModel = viewModel(
        factory = serviciosFactory
    )

    fun cerrarSesion() {
        sesionViewModel.cerrarSesion()
        navController.navigate(Rutas.LOGIN) {
            popUpTo(Rutas.SPLASH) { inclusive = true }
            launchSingleTop = true
        }
    }

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
            FormularioMascotaScreen(
                viewModel = mascotaViewModel,
                duenoId = duenoId,
                mascotaId = entry.arguments
                    ?.getString("mascotaId")?.toIntOrNull(),
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

        composable(Rutas.PERFIL) {
            val mascotas by inicioViewModel.mascotas.collectAsState()
            val proximas by misCitasViewModel.proximas.collectAsState()
            val historial by misCitasViewModel.historial.collectAsState()

            PerfilScreen(
                nombre = usuario?.nombre ?: "Usuario",
                correo = usuario?.correo ?: "",
                cantidadMascotas = mascotas.size,
                citasProximas = proximas.size,
                atenciones = historial.count {
                    it.cita.estado == "ATENDIDA"
                },
                modoOscuro = modoOscuro,
                notificaciones = notificaciones,
                onModoOscuro = sesionViewModel::cambiarModoOscuro,
                onNotificaciones = cambiarNotificaciones,
                onCambiarPerfil = { cerrarSesion() },
                onCerrarSesion = { cerrarSesion() },
                onVolver = { navController.popBackStack() }
            )
        }


        composable(Rutas.PANEL) {
            PanelScreen(
                viewModel = panelViewModel,
                onAgenda = {
                    navController.navigate(Rutas.AGENDA)
                },
                onPacientes = {
                    navController.navigate(Rutas.PACIENTES)
                },
                onServicios = {
                    navController.navigate(Rutas.SERVICIOS)
                },
                onAjustes = {
                    navController.navigate(Rutas.AJUSTES)
                },
                onCerrarSesion = {
                    cerrarSesion()
                }
            )
        }


        composable(Rutas.AGENDA) {
            AgendaScreen(
                viewModel = agendaViewModel,
                onVolver = { navController.popBackStack() },
                onAtender = { id ->
                    navController.navigate(Rutas.atenderCita(id))
                }
            )
        }

        composable(Rutas.ATENDER_CITA) { entry ->
            val citaId = entry.arguments
                ?.getString("citaId")?.toIntOrNull() ?: 0

            val factory = remember(repository, citaId) {
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T = AtenderCitaViewModel(repository, citaId) as T
                }
            }

            val vm: AtenderCitaViewModel = viewModel(
                key = "atender_$citaId",
                factory = factory
            )

            AtenderCitaScreen(
                viewModel = vm,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.PACIENTES) {
            PacientesScreen(
                viewModel = pacientesViewModel,
                onVolver = { navController.popBackStack() },
                onFicha = { id ->
                    navController.navigate(Rutas.fichaMascota(id))
                }
            )
        }

        composable(Rutas.SERVICIOS) {
            ServiciosScreen(
                viewModel = serviciosViewModel,
                onVolver = { navController.popBackStack() },
                onNuevo = {
                    navController.navigate(Rutas.FORMULARIO_SERVICIO)
                },
                onEditar = { id ->
                    navController.navigate("formulario_servicio/$id")
                }
            )
        }

        composable(Rutas.FORMULARIO_SERVICIO) {
            FormularioServicioScreen(
                viewModel = serviciosViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable("formulario_servicio/{servicioId}") { entry ->
            FormularioServicioScreen(
                viewModel = serviciosViewModel,
                servicioId = entry.arguments
                    ?.getString("servicioId")?.toIntOrNull(),
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.AJUSTES) {
            AjustesScreen(
                nombre = usuario?.nombre ?: "Veterinario",
                correo = usuario?.correo ?: "",
                modoOscuro = modoOscuro,
                notificaciones = notificaciones,
                aceptarCitas = aceptarCitas,
                onModoOscuro = sesionViewModel::cambiarModoOscuro,
                onNotificaciones = cambiarNotificaciones,
                onAceptarCitas = cambiarAceptarCitas,
                onCambiarPerfil = { cerrarSesion() },
                onCerrarSesion = { cerrarSesion() },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
