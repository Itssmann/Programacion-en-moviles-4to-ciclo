package com.abad.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.murocampus.model.Post
import com.abad.murocampus.ui.theme.degradadoEncabezado
import com.abad.murocampus.viewmodel.PostsViewModel
import com.abad.murocampus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrirPost: (Int) -> Unit,
    onCrear: () -> Unit
) {
    val estado by vm.listaVisible.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val usuarioFiltro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val refrescando by vm.refrescando.collectAsStateWithLifecycle()
    val estadoLista = rememberLazyListState()
    val alInicio by remember { derivedStateOf { estadoLista.firstVisibleItemIndex == 0 } }
    val margenInferior = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { AvisosSnackbar(vm) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCrear,
                expanded = alInicio,
                icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                text = { Text("Publicar") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(bottom = margenInferior)
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = refrescando,
            onRefresh = { vm.refrescar() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = estadoLista,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = margenInferior + 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val posts = (estado as? UiState.Exito)?.datos
                    Encabezado(
                        busqueda = busqueda,
                        onBuscar = { vm.buscar(it) },
                        publicaciones = posts?.size,
                        autores = posts?.map { it.userId }?.distinct()?.size
                    )
                }
                item {
                    FilaAutores(
                        seleccionado = usuarioFiltro,
                        onSeleccionar = { vm.filtrar(it) }
                    )
                }
                when (val e = estado) {
                    is UiState.Cargando -> items(4) {
                        TarjetaEsqueleto(Modifier.padding(horizontal = 16.dp))
                    }
                    is UiState.Error -> item {
                        Box(Modifier.fillParentMaxHeight(0.6f)) {
                            ErrorConReintento(mensaje = e.mensaje, onReintentar = { vm.cargar() })
                        }
                    }
                    is UiState.Exito -> when {
                        e.datos.isNotEmpty() -> {
                            item { Resumen(e.datos, usuarioFiltro, busqueda) }
                            items(e.datos, key = { it.id }) { post ->
                                TarjetaPost(
                                    userId = post.userId,
                                    titulo = post.title,
                                    cuerpo = post.body,
                                    onClick = { onAbrirPost(post.id) },
                                    resaltar = busqueda,
                                    idPost = post.id,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                        busqueda.isNotBlank() -> item {
                            Box(Modifier.fillParentMaxHeight(0.5f)) {
                                EstadoVacio(
                                    icono = Icons.Filled.SearchOff,
                                    titulo = "Sin resultados",
                                    mensaje = "Ningún título contiene \"${busqueda.trim()}\"."
                                )
                            }
                        }
                        else -> item {
                            Box(Modifier.fillParentMaxHeight(0.5f)) {
                                EstadoVacio(
                                    icono = Icons.Filled.Inbox,
                                    titulo = "Sin publicaciones",
                                    mensaje = "Este usuario todavía no ha publicado nada."
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Encabezado(
    busqueda: String,
    onBuscar: (String) -> Unit,
    publicaciones: Int?,
    autores: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = degradadoEncabezado(),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp)
    ) {
        Text(
            text = "MuroCampus",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Text(
            text = "Lo que se comparte hoy en tu campus",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
        Spacer(Modifier.height(20.dp))
        CampoBusqueda(texto = busqueda, onCambiar = onBuscar)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Estadistica(
                valor = publicaciones?.toString() ?: "—",
                etiqueta = if (publicaciones == 1) "publicación" else "publicaciones"
            )
            Estadistica(
                valor = autores?.toString() ?: "—",
                etiqueta = if (autores == 1) "autor" else "autores"
            )
        }
    }
}

@Composable
private fun Estadistica(valor: String, etiqueta: String) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.16f), CircleShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun CampoBusqueda(texto: String, onCambiar: (String) -> Unit) {
    val teclado = LocalSoftwareKeyboardController.current
    TextField(
        value = texto,
        onValueChange = onCambiar,
        placeholder = { Text("Buscar por título") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onCambiar("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Borrar búsqueda")
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { teclado?.hide() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.18f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.18f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedPlaceholderColor = Color.White.copy(alpha = 0.75f),
            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.75f),
            focusedLeadingIconColor = Color.White,
            unfocusedLeadingIconColor = Color.White,
            focusedTrailingIconColor = Color.White,
            unfocusedTrailingIconColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FilaAutores(seleccionado: Int?, onSeleccionar: (Int?) -> Unit) {
    val opciones: List<Int?> = listOf(null) + (1..10).toList()

    Column {
        Text(
            text = "Autores",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 10.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(opciones) { userId ->
                AutorCircular(
                    userId = userId,
                    activo = seleccionado == userId,
                    onClick = { onSeleccionar(userId) }
                )
            }
        }
    }
}

@Composable
private fun AutorCircular(userId: Int?, activo: Boolean, onClick: () -> Unit) {
    val borde = if (activo) {
        BorderStroke(3.dp, degradadoEncabezado())
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(borde, CircleShape)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            if (userId == null) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                AvatarUsuario(userId, tamano = 54.dp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (userId == null) "Todos" else primerNombreDe(userId),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
            color = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Resumen(posts: List<Post>, usuarioFiltro: Int?, busqueda: String) {
    val origen = if (usuarioFiltro == null) "de toda la comunidad" else "de ${nombreDe(usuarioFiltro)}"
    val texto = if (busqueda.isBlank()) {
        "${posts.size} publicaciones $origen"
    } else {
        "${posts.size} resultados para \"${busqueda.trim()}\""
    }
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 20.dp, top = 8.dp)
    )
}
