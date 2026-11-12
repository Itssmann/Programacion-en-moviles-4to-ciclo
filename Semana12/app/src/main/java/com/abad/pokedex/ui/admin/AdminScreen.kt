package com.abad.pokedex.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abad.pokedex.ui.components.*

@Composable
fun AdminScreen(
    totalPokemones: Int,
    totalFavoritos: Int,
    onLimpiarCache: () -> Unit,
    onSincronizar: () -> Unit,
    cargando: Boolean = false,
    mensaje: String? = null,
    nombre: String? = null,
    distribucionTipos: Map<String, Int> = emptyMap(),
    datosCargados: Boolean = true,
    esError: Boolean = false,
    operacion: OperacionAdmin? = null
) {
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 960.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)) {
            HeroPokemon(listOf(Color(0xFF172844), Color(0xFF25456B), Color(0xFF386287))) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFD17A), contentColor = Color(0xFF332400)) {
                        Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.padding(10.dp).size(24.dp))
                    }
                    Text("ADMINISTRACIÓN", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFFFFD17A),
                        modifier = Modifier.weight(1f))
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Centro de control\nPokédex", style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(nombre?.takeIf { it.isNotBlank() }?.let { "Hola, $it" } ?: "Gestiona tu catálogo Pokémon",
                        style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.9f))
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.18f))
                Text("Sincroniza datos, revisa tu colección y administra el almacenamiento local.",
                    style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                TituloSeccion("El catálogo de un vistazo", "Datos de este dispositivo · favoritos de tu cuenta")
                EstadisticasColeccion(totalPokemones.takeIf { datosCargados }, totalFavoritos.takeIf { datosCargados })
            }

            AnimatedVisibility(visible = mensaje != null) {
                Surface(shape = RoundedCornerShape(20.dp),
                    color = if (esError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (esError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }) {
                    Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        Icon(if (esError) Icons.Default.ErrorOutline else Icons.Default.CheckCircleOutline, null)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(if (esError) "No se completó la operación" else "Operación completada", fontWeight = FontWeight.Bold)
                            Text(mensaje.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Card(Modifier.fillMaxWidth().animateContentSize(), shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    TituloSeccion("Diversidad Pokémon", "Distribución por tipos del catálogo descargado.")
                    if (!datosCargados) LinearProgressIndicator(Modifier.fillMaxWidth())
                    else if (distribucionTipos.isEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CatchingPokemon, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.outline)
                            Text("Sincroniza el catálogo para ver aquí su distribución.", modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        distribucionTipos.forEach { (tipo, cantidad) ->
                            val proporcion by animateFloatAsState(
                                targetValue = if (totalPokemones > 0) (cantidad.toFloat() / totalPokemones).coerceIn(0f, 1f) else 0f,
                                label = "distribucion_$tipo"
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    EtiquetaTipo(tipo)
                                    Text(cantidad.toString(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                                LinearProgressIndicator(progress = { proporcion }, modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                            }
                        }
                        Text("Un Pokémon puede pertenecer a más de un tipo.", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            TituloSeccion("Gestión del catálogo", "Acciones disponibles para administradores.")
            Card(Modifier.fillMaxWidth().animateContentSize(), shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.CloudSync, null, modifier = Modifier.size(36.dp))
                    Text("Trae el mundo Pokémon\na tu Pokédex", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Descarga el catálogo desde PokéAPI y actualiza los datos disponibles sin conexión. Tus favoritos se conservan.",
                        style = MaterialTheme.typography.bodyMedium)
                    if (cargando && operacion != OperacionAdmin.LIMPIAR) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text("Sincronizando el catálogo…", style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                    }
                    Button(onClick = onSincronizar, enabled = !cargando, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary)) {
                        Icon(Icons.Default.Sync, null)
                        Spacer(Modifier.width(10.dp))
                        Text("Sincronizar catálogo", modifier = Modifier.weight(1f))
                    }
                }
            }

            OutlinedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.CleaningServices, null, tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(10.dp).size(24.dp))
                        }
                        Text("Mantenimiento local", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f))
                    }
                    Text("Libera la caché del catálogo en este dispositivo. Las marcas de favoritos de tu cuenta se conservan localmente; los Pokémon volverán a mostrarse cuando sincronices.",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (cargando && operacion == OperacionAdmin.LIMPIAR) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text("Limpiando caché…", style = MaterialTheme.typography.labelLarge)
                    }
                    OutlinedButton(onClick = { mostrarConfirmacion = true }, enabled = !cargando,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Icon(Icons.Default.DeleteSweep, null)
                        Spacer(Modifier.width(10.dp))
                        Text("Limpiar caché local", modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
    if (mostrarConfirmacion) AlertDialog(
        onDismissRequest = { mostrarConfirmacion = false },
        icon = { Icon(Icons.Default.DeleteSweep, null) },
        title = { Text("¿Limpiar caché local?") },
        text = { Text("Se eliminarán los datos descargados del catálogo. Las marcas de favoritos se conservarán en este dispositivo. Necesitarás conexión para volver a descargar los Pokémon.") },
        confirmButton = {
            TextButton(enabled = !cargando, onClick = { mostrarConfirmacion = false; onLimpiarCache() }) {
                Text("Limpiar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") } }
    )
}
