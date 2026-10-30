package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.data.local.entity.Usuario
import com.abad.huellitasvet.data.local.relation.CitaConDetalle
import com.abad.huellitasvet.ui.components.EstadoCitaChip
import com.abad.huellitasvet.ui.theme.HuellitasVetTheme
import com.abad.huellitasvet.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onCerrarSesion: () -> Unit
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val mascotas by viewModel.mascotas.collectAsState()
    val citas by viewModel.citas.collectAsState()
    val servicios by viewModel.servicios.collectAsState()
    val usuariosFiltrados by viewModel.usuariosFiltrados.collectAsState()
    val citasFiltradas by viewModel.citasFiltradas.collectAsState()
    val busqueda by viewModel.busquedaUsuario.collectAsState()
    val filtroRol by viewModel.filtroRol.collectAsState()
    val filtroEstadoCita by viewModel.filtroEstadoCita.collectAsState()

    var pestana by remember { mutableIntStateOf(0) }
    var servicioEditando by remember { mutableStateOf<Servicio?>(null) }
    var mostrarDialogoServicio by remember { mutableStateOf(false) }

    val totalClientes = usuarios.count { it.rol == "CLIENTE" }
    val totalVets = usuarios.count { it.rol == "VET" }
    val totalAdmins = usuarios.count { it.rol == "ADMIN" }

    HuellitasVetTheme(role = "ADMIN") {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Panel de Administración", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onCerrarSesion) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = pestana == 0,
                        onClick = { pestana = 0 },
                        icon = { Text("📊") },
                        label = { Text("Resumen") }
                    )
                    NavigationBarItem(
                        selected = pestana == 1,
                        onClick = { pestana = 1 },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Usuarios") }
                    )
                    NavigationBarItem(
                        selected = pestana == 2,
                        onClick = { pestana = 2 },
                        icon = { Text("📅") },
                        label = { Text("Citas") }
                    )
                    NavigationBarItem(
                        selected = pestana == 3,
                        onClick = { pestana = 3 },
                        icon = { Text("🛠️") },
                        label = { Text("Servicios") }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (pestana) {
                    0 -> AdminResumenTab(usuarios.size, totalClientes, totalVets, totalAdmins, mascotas.size, citas.size, servicios.size)
                    1 -> AdminUsuariosTab(
                        usuarios = usuariosFiltrados,
                        busqueda = busqueda,
                        onBusquedaChange = viewModel::buscarUsuario,
                        filtroRol = filtroRol,
                        onFiltroRolChange = viewModel::filtrarRol
                    )
                    2 -> AdminCitasTab(
                        citas = citasFiltradas,
                        filtroEstado = filtroEstadoCita,
                        onFiltroEstadoChange = viewModel::filtrarEstadoCita
                    )
                    3 -> AdminServiciosTab(
                        servicios = servicios,
                        onAgregar = {
                            servicioEditando = Servicio(0, "", "", 30, 0.0, true, "heart")
                            mostrarDialogoServicio = true
                        },
                        onEditar = { s ->
                            servicioEditando = s
                            mostrarDialogoServicio = true
                        },
                        onEliminar = viewModel::eliminarServicio
                    )
                }
            }
        }

        if (mostrarDialogoServicio && servicioEditando != null) {
            FormularioServicioDialog(
                servicio = servicioEditando!!,
                onDismiss = { mostrarDialogoServicio = false },
                onGuardar = { s ->
                    viewModel.guardarServicio(s)
                    mostrarDialogoServicio = false
                }
            )
        }
    }
}

@Composable
fun AdminResumenTab(
    totalUsuarios: Int,
    totalClientes: Int,
    totalVets: Int,
    totalAdmins: Int,
    numMascotas: Int,
    numCitas: Int,
    numServicios: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Resumen General de HuellitasVet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Total Usuarios", "$totalUsuarios", Color(0xFFEDE9FE), Color(0xFF5B21B6), Modifier.weight(1f))
            MetricCard("Mascotas", "$numMascotas", Color(0xFFD1FAE5), Color(0xFF065F46), Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Dueños", "$totalClientes", Color(0xFFFFEDD5), Color(0xFF7C2D12), Modifier.weight(1f))
            MetricCard("Veterinarios", "$totalVets", Color(0xFFFEF3C7), Color(0xFF713F12), Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Citas", "$numCitas", Color(0xFFDBEAFE), Color(0xFF1D4ED8), Modifier.weight(1f))
            MetricCard("Servicios", "$numServicios", Color(0xFFE2E8F0), Color(0xFF334155), Modifier.weight(1f))
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nota de seguridad académica", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(
                    "Esta cuenta de Administrador tiene acceso global de gestión. " +
                            "La autorización local en el dispositivo es únicamente para fines de demostración académica y evaluación, " +
                            "y no sustituye un sistema de control de acceso basado en servidor seguro.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MetricCard(titulo: String, valor: String, containerColor: Color, contentColor: Color, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(valor, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = contentColor)
            Text(titulo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = contentColor)
        }
    }
}

@Composable
fun AdminUsuariosTab(
    usuarios: List<Usuario>,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    filtroRol: String,
    onFiltroRolChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Gestión de Usuarios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            label = { Text("Buscar por nombre o correo") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val roles = listOf("TODOS", "CLIENTE", "VET", "ADMIN")
            items(roles) { r ->
                FilterChip(
                    selected = filtroRol == r,
                    onClick = { onFiltroRolChange(r) },
                    label = { Text(r) }
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(usuarios, key = { it.id }) { u ->
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                            Text(u.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(u.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (!u.telefono.isNullOrBlank() || !u.distrito.isNullOrBlank()) {
                                Text("Tel: ${u.telefono ?: "N/D"} · Dist: ${u.distrito ?: "N/D"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (u.rol) {
                                "ADMIN" -> Color(0xFFFEE2E2)
                                "VET" -> Color(0xFFFEF3C7)
                                else -> Color(0xFFD1FAE5)
                            }
                        ) {
                            Text(
                                u.rol,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (u.rol) {
                                    "ADMIN" -> Color(0xFFB91C1C)
                                    "VET" -> Color(0xFF713F12)
                                    else -> Color(0xFF065F46)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCitasTab(
    citas: List<CitaConDetalle>,
    filtroEstado: String,
    onFiltroEstadoChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Supervisión de Citas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val estados = listOf("TODOS", "PENDIENTE", "CONFIRMADA", "ATENDIDA", "CANCELADA")
            items(estados) { est ->
                FilterChip(
                    selected = filtroEstado == est,
                    onClick = { onFiltroEstadoChange(est) },
                    label = { Text(est) }
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(citas, key = { it.cita.id }) { detalle ->
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("📅 ${detalle.cita.fecha} · ${detalle.cita.hora}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            EstadoCitaChip(detalle.cita.estado)
                        }
                        Text("Mascota: ${detalle.mascota.mascota.nombre} (${detalle.mascota.mascota.especie})", style = MaterialTheme.typography.bodySmall)
                        Text("Servicio principal: ${detalle.servicio.nombre} (S/ ${"%.2f".format(detalle.cita.precio)})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        if (detalle.cita.nota.isNotBlank()) {
                            Text("Nota: ${detalle.cita.nota}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminServiciosTab(
    servicios: List<Servicio>,
    onAgregar: () -> Unit,
    onEditar: (Servicio) -> Unit,
    onEliminar: (Servicio) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Gestión de Servicios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Button(onClick = onAgregar, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Nuevo")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(servicios, key = { it.id }) { s ->
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                            Text(s.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(s.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("S/ ${"%.2f".format(s.precio)} · ${s.duracionMin} min · ${if (s.activo) "Activo" else "Inactivo"}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (s.activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        }
                        Row {
                            IconButton(onClick = { onEditar(s) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onEliminar(s) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioServicioDialog(
    servicio: Servicio,
    onDismiss: () -> Unit,
    onGuardar: (Servicio) -> Unit
) {
    var nombre by remember { mutableStateOf(servicio.nombre) }
    var descripcion by remember { mutableStateOf(servicio.descripcion) }
    var precioText by remember { mutableStateOf(if (servicio.precio > 0) servicio.precio.toString() else "") }
    var duracionText by remember { mutableStateOf(if (servicio.duracionMin > 0) servicio.duracionMin.toString() else "30") }
    var activo by remember { mutableStateOf(servicio.activo) }
    var icono by remember { mutableStateOf(servicio.icono.ifBlank { "heart" }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (servicio.id == 0) "Nuevo Servicio" else "Editar Servicio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precioText,
                    onValueChange = { precioText = it },
                    label = { Text("Precio (S/)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = duracionText,
                    onValueChange = { duracionText = it },
                    label = { Text("Duración (minutos)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Servicio activo", fontWeight = FontWeight.Bold)
                    Switch(checked = activo, onCheckedChange = { activo = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val p = precioText.toDoubleOrNull() ?: 0.0
                    val d = duracionText.toIntOrNull() ?: 30
                    if (nombre.isNotBlank() && p > 0) {
                        onGuardar(
                            servicio.copy(
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                precio = p,
                                duracionMin = d,
                                activo = activo,
                                icono = icono
                            )
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
