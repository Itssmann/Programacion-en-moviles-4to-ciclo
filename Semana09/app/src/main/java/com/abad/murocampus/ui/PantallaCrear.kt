package com.abad.murocampus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.murocampus.viewmodel.PostsViewModel
import com.abad.murocampus.viewmodel.UiState

private const val MAX_TITULO = 80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrear(vm: PostsViewModel, onVolver: () -> Unit) {
    val estado by vm.publicacion.collectAsStateWithLifecycle()
    var titulo by rememberSaveable { mutableStateOf("") }
    var texto by rememberSaveable { mutableStateOf("") }

    val volver = {
        vm.reiniciarPublicacion()
        onVolver()
    }
    BackHandler { volver() }

    LaunchedEffect(estado) {
        if (estado is UiState.Exito) {
            titulo = ""
            texto = ""
        }
    }

    val enviando = estado is UiState.Cargando
    val puedePublicar = titulo.isNotBlank() && texto.isNotBlank() && !enviando

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva publicación") },
                navigationIcon = {
                    IconButton(onClick = volver) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarUsuario(1, tamano = 44.dp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Usuario 1", style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Public,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Visible para todo el campus",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { if (it.length <= MAX_TITULO) titulo = it },
                        label = { Text("Título") },
                        singleLine = true,
                        supportingText = { Text("${titulo.length}/$MAX_TITULO") },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = texto,
                        onValueChange = { texto = it },
                        label = { Text("¿Qué quieres compartir?") },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp)
                    )
                }
            }

            Button(
                onClick = { vm.publicar(titulo.trim(), texto.trim()) },
                enabled = puedePublicar,
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (enviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                }
                Spacer(Modifier.width(8.dp))
                Text(if (enviando) "Publicando..." else "Publicar")
            }

            AnimatedVisibility(visible = estado is UiState.Exito || estado is UiState.Error) {
                when (val e = estado) {
                    is UiState.Exito -> AvisoResultado(
                        icono = Icons.Filled.CheckCircle,
                        titulo = "¡Publicado!",
                        texto = "Publicado con id ${e.datos.id}",
                        fondo = MaterialTheme.colorScheme.tertiaryContainer,
                        contenido = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    is UiState.Error -> AvisoResultado(
                        icono = Icons.Filled.ErrorOutline,
                        titulo = "No se pudo publicar",
                        texto = e.mensaje,
                        fondo = MaterialTheme.colorScheme.errorContainer,
                        contenido = MaterialTheme.colorScheme.onErrorContainer
                    )
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun AvisoResultado(
    icono: ImageVector,
    titulo: String,
    texto: String,
    fondo: Color,
    contenido: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = fondo, contentColor = contenido)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(texto, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
