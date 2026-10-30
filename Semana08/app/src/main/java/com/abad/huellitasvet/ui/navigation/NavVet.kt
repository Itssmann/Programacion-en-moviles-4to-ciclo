package com.abad.huellitasvet.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import com.abad.huellitasvet.data.repository.VetRepository
import com.abad.huellitasvet.ui.theme.HuellitasVetTheme
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
    val perfilActivo by sesionViewModel.perfilActivo.collectAsState()

    val isAdmin = usuario?.rol == "ADMIN" || perfilActivo == "ADMIN"

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

    val pedirCitaFactory = remember(repository, duenoId, preferencias) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                PedirCitaViewModel(repository, duenoId, preferencias) as T
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

    val ownerNavegar: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.INICIO) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    val activeRole = usuario?.rol ?: perfilActivo

    HuellitasVetTheme(isAdmin = isAdmin, role = activeRole, darkTheme = modoOscuro) {
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
                        when (rol) {
                            "ADMIN" -> Rutas.ADMIN_DASHBOARD
                            "VET" -> Rutas.PANEL
                            else -> Rutas.INICIO
                        }
                    ) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegistrar = {
                    navController.navigate(Rutas.REGISTRO)
                }
            )
        }

        composable(Rutas.REGISTRO) {
            val registroViewModel: RegistroViewModel = viewModel(
                factory = VetViewModelFactory(repository, preferencias)
            )
            RegistroScreen(
                viewModel = registroViewModel,
                onRegistroExitoso = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onVolverLogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Rutas.INICIO) {
            val nombreDinamico = usuario?.nombre?.ifBlank { null } ?: "Usuario"

            InicioScreen(
                nombreDueno = nombreDinamico,
                viewModel = inicioViewModel,
                onMascotas = { navController.navigate(Rutas.MASCOTAS) },
                onAgregar = {
                    navController.navigate(Rutas.FORMULARIO_MASCOTA)
                },
                onPedirCita = { navController.navigate(Rutas.PEDIR_CITA) },
                onMisCitas = { navController.navigate(Rutas.MIS_CITAS) },
                onNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) },
                bottomBar = {
                    OwnerBottomBar(currentRoute = Rutas.INICIO, onNavigate = ownerNavegar)
                }
            )
        }

        composable(Rutas.MASCOTAS) {
            MascotasScreen(
                viewModel = inicioViewModel,
                onAgregar = {
                    navController.navigate(Rutas.FORMULARIO_MASCOTA)
                },
                onAbrirMascota = { id ->
                    navController.navigate(Rutas.fichaMascota(id))
                },
                bottomBar = {
                    OwnerBottomBar(currentRoute = Rutas.MASCOTAS, onNavigate = ownerNavegar)
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
                onPedirCita = { navController.navigate(Rutas.PEDIR_CITA) },
                bottomBar = {
                    OwnerBottomBar(currentRoute = Rutas.MASCOTAS, onNavigate = ownerNavegar)
                }
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
                onPedirCita = { navController.navigate(Rutas.PEDIR_CITA) },
                bottomBar = {
                    OwnerBottomBar(currentRoute = Rutas.MIS_CITAS, onNavigate = ownerNavegar)
                }
            )
        }

        composable(Rutas.PERFIL) {
            val mascotas by inicioViewModel.mascotas.collectAsState()
            val proximas by misCitasViewModel.proximas.collectAsState()
            val historial by misCitasViewModel.historial.collectAsState()

            PerfilScreen(
                nombre = usuario?.nombre ?: "Usuario",
                correo = usuario?.correo ?: "",
                telefono = usuario?.telefono,
                distrito = usuario?.distrito,
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
                bottomBar = {
                    OwnerBottomBar(currentRoute = Rutas.PERFIL, onNavigate = ownerNavegar)
                }
            )
        }


        composable(Rutas.NOTIFICACIONES) {
            val notificacionesFactory = remember(repository, duenoId) {
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        NotificacionesViewModel(repository, duenoId) as T
                }
            }
            val notificacionesViewModel: NotificacionesViewModel = viewModel(
                key = "notificaciones_$duenoId",
                factory = notificacionesFactory
            )
            NotificacionesScreen(
                viewModel = notificacionesViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.ADMIN_DASHBOARD) {
            val adminFactory = remember(repository) {
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        AdminViewModel(repository) as T
                }
            }
            val adminViewModel: AdminViewModel = viewModel(factory = adminFactory)
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onCerrarSesion = { cerrarSesion() }
            )
        }

        composable(Rutas.PANEL) {
            PanelScreen(
                viewModel = panelViewModel,
                onAgenda = {
                    navController.navigate(Rutas.AGENDA) { launchSingleTop = true }
                },
                onNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) },
                bottomBar = {
                    AdminBottomBar(currentRoute = Rutas.PANEL) { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }


        composable(Rutas.AGENDA) {
            AgendaScreen(
                viewModel = agendaViewModel,
                onAtender = { id ->
                    navController.navigate(Rutas.atenderCita(id))
                },
                bottomBar = {
                    AdminBottomBar(currentRoute = Rutas.AGENDA) { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
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
                onFicha = { id ->
                    navController.navigate(Rutas.fichaMascota(id))
                },
                bottomBar = {
                    AdminBottomBar(currentRoute = Rutas.PACIENTES) { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        composable(Rutas.SERVICIOS) {
            ServiciosScreen(
                viewModel = serviciosViewModel,
                onNuevo = {
                    navController.navigate(Rutas.FORMULARIO_SERVICIO)
                },
                onEditar = { id ->
                    navController.navigate("formulario_servicio/$id")
                },
                bottomBar = {
                    AdminBottomBar(currentRoute = Rutas.SERVICIOS) { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
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
                bottomBar = {
                    AdminBottomBar(currentRoute = Rutas.AJUSTES) { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(Rutas.PANEL) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
  }
}

@Composable
fun AdminBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            selected = currentRoute == Rutas.PANEL,
            onClick = { onNavigate(Rutas.PANEL) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Panel", modifier = Modifier.size(24.dp)) },
            label = { Text("Panel") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == Rutas.AGENDA,
            onClick = { onNavigate(Rutas.AGENDA) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Agenda", modifier = Modifier.size(24.dp)) },
            label = { Text("Agenda") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == Rutas.PACIENTES,
            onClick = { onNavigate(Rutas.PACIENTES) },
            icon = { Icon(Icons.Default.Pets, contentDescription = "Pacientes", modifier = Modifier.size(24.dp)) },
            label = { Text("Pacientes") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == Rutas.SERVICIOS,
            onClick = { onNavigate(Rutas.SERVICIOS) },
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Servicios", modifier = Modifier.size(24.dp)) },
            label = { Text("Servicios") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == Rutas.AJUSTES,
            onClick = { onNavigate(Rutas.AJUSTES) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes", modifier = Modifier.size(24.dp)) },
            label = { Text("Ajustes") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
fun OwnerBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            selected = currentRoute == Rutas.INICIO,
            onClick = { onNavigate(Rutas.INICIO) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio", modifier = Modifier.size(24.dp)) },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == Rutas.MASCOTAS,
            onClick = { onNavigate(Rutas.MASCOTAS) },
            icon = { Icon(Icons.Default.Pets, contentDescription = "Mascotas", modifier = Modifier.size(24.dp)) },
            label = { Text("Mascotas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == Rutas.MIS_CITAS,
            onClick = { onNavigate(Rutas.MIS_CITAS) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Citas", modifier = Modifier.size(24.dp)) },
            label = { Text("Citas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute == Rutas.PERFIL,
            onClick = { onNavigate(Rutas.PERFIL) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.size(24.dp)) },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
