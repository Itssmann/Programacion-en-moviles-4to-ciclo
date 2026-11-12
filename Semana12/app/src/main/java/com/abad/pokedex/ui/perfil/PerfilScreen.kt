package com.abad.pokedex.ui.perfil

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abad.pokedex.domain.model.Rol
import com.abad.pokedex.ui.components.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerfilScreen(
    correo: String?,
    nombre: String?,
    onLogin: () -> Unit,
    onCerrarSesion: () -> Unit,
    rol: Rol? = null,
    coleccion: PerfilUiState = PerfilUiState(),
    onFavoritos: () -> Unit = {}
) {
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }
    val nombreVisible = nombre?.takeIf { it.isNotBlank() } ?: if (correo == null) "Entrenador invitado" else "Entrenador Pokémon"
    val rolVisible = when {
        correo == null -> "Invitado"
        rol == Rol.ADMIN -> "Administrador"
        rol == Rol.USUARIO -> "Usuario"
        else -> "Rol no disponible"
    }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 840.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TituloSeccion("Mi perfil", "Tu identidad y tu colección, en un solo lugar.")
            HeroPokemon(listOf(Color(0xFF691C35), Color(0xFFB82F48), Color(0xFFD5444D))) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CatchingPokemon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text("POKÉDEX ABAD · ENTRENADORES", style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.85f), modifier = Modifier.weight(1f))
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.16f)) {
                        Box(Modifier.size(92.dp).padding(7.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                            if (!nombre.isNullOrBlank()) {
                                val iniciales = nombre.trim().split(Regex("\\s+")).take(2).mapNotNull { it.firstOrNull() }.joinToString("").uppercase()
                                Text(iniciales, color = Color(0xFF8C2540), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                            } else Icon(Icons.Default.Person, null, tint = Color(0xFF8C2540), modifier = Modifier.size(48.dp))
                        }
                    }
                    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.16f), contentColor = Color.White) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(if (rol == Rol.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.PersonOutline,
                                null, modifier = Modifier.size(18.dp))
                            Text(rolVisible, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(nombreVisible, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text(correo ?: "Inicia sesión para guardar tus favoritos", color = Color.White.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.22f))
                Text("CARNET DE ENTRENADOR", style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Bold)
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                TituloSeccion("Mi colección", "El catálogo guardado en este dispositivo.")
                EstadisticasColeccion(
                    total = coleccion.totalPokemones.takeUnless { coleccion.cargando || coleccion.error != null },
                    favoritos = coleccion.totalFavoritos.takeUnless { coleccion.cargando || coleccion.error != null }
                )
                coleccion.error?.let {
                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(16.dp)) {
                        Text(it, Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            Card(Modifier.fillMaxWidth().animateContentSize(), shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Favorite, null, tint = MaterialTheme.colorScheme.primary)
                        Text("Tus favoritos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    if (coleccion.cargando) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text("Consultando tu colección…", style = MaterialTheme.typography.bodyMedium)
                    }
                    AnimatedVisibility(visible = !coleccion.cargando && coleccion.favoritos.isNotEmpty(), enter = fadeIn() + expandVertically()) {
                        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            coleccion.favoritos.take(3).forEach { pokemon ->
                                Column(Modifier.width(96.dp).clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow).padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    PokemonImage(pokemon.imagenUrl, pokemon.nombre, Modifier.size(76.dp))
                                    Text(pokemon.nombre.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelLarge,
                                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                    if (!coleccion.cargando && coleccion.favoritos.isEmpty()) {
                        Text(if (coleccion.totalFavoritos > 0)
                            "Tus favoritos siguen marcados. Descarga de nuevo el catálogo para ver sus imágenes aquí."
                        else "Marca el corazón de un Pokémon para encontrarlo aquí.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    }
                    FilledTonalButton(onClick = onFavoritos, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text("Ver mis favoritos", modifier = Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                TituloSeccion("Información de cuenta", "Los datos de tu sesión actual.")
                OutlinedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DatoCuenta(Icons.Default.PersonOutline, "Nombre", nombre?.takeIf { it.isNotBlank() } ?: "Sin nombre registrado")
                        HorizontalDivider()
                        DatoCuenta(Icons.Default.AlternateEmail, "Correo electrónico", correo ?: "Sin sesión iniciada")
                        HorizontalDivider()
                        DatoCuenta(Icons.Default.VerifiedUser, "Rol de la cuenta", rolVisible)
                    }
                }
            }
            if (correo == null) {
                Button(onClick = onLogin, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                    Text("Iniciar sesión o registrarse")
                }
            } else {
                OutlinedButton(onClick = { confirmarSalida = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                    Spacer(Modifier.width(10.dp))
                    Text("Cerrar sesión")
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
    if (confirmarSalida) AlertDialog(
        onDismissRequest = { confirmarSalida = false },
        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
        title = { Text("¿Cerrar sesión?") },
        text = { Text("Volverás a la pantalla de bienvenida. Tus favoritos se conservarán para tu cuenta en este dispositivo.") },
        confirmButton = { TextButton(onClick = { confirmarSalida = false; onCerrarSesion() }) { Text("Cerrar sesión") } },
        dismissButton = { TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") } }
    )
}

@Composable
private fun DatoCuenta(icono: ImageVector, etiqueta: String, valor: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
            Icon(icono, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(10.dp).size(22.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
