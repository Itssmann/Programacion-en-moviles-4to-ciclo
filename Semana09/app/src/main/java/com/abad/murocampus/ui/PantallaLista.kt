package com.abad.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.murocampus.model.Post
import com.abad.murocampus.viewmodel.PostsViewModel
import com.abad.murocampus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrirPost: (Int) -> Unit,
    onCrear: () -> Unit
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val usuarioFiltro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val refrescando by vm.refrescando.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("MuroCampus") },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCrear,
                expanded = scrollBehavior.state.collapsedFraction < 0.5f,
                icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                text = { Text("Publicar") },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FiltroUsuarios(
                seleccionado = usuarioFiltro,
                onSeleccionar = { vm.filtrar(it) }
            )
            PullToRefreshBox(
                isRefreshing = refrescando,
                onRefresh = { vm.refrescar() },
                modifier = Modifier.fillMaxSize()
            ) {
                when (val e = estado) {
                    is UiState.Cargando -> ListaEsqueleto()
                    is UiState.Error -> ErrorConReintento(
                        mensaje = e.mensaje,
                        onReintentar = { vm.cargar() }
                    )
                    is UiState.Exito ->
                        if (e.datos.isEmpty()) {
                            EstadoVacio(
                                icono = Icons.Filled.Inbox,
                                titulo = "Sin publicaciones",
                                mensaje = "Este usuario todavía no ha publicado nada."
                            )
                        } else {
                            ListaPosts(e.datos, usuarioFiltro, onAbrirPost)
                        }
                }
            }
        }
    }
}

@Composable
private fun FiltroUsuarios(seleccionado: Int?, onSeleccionar: (Int?) -> Unit) {
    val opciones: List<Int?> = listOf(null) + (1..10).toList()

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { userId ->
            val activo = seleccionado == userId
            FilterChip(
                selected = activo,
                onClick = { onSeleccionar(userId) },
                label = { Text(if (userId == null) "Todos" else "Usuario $userId") },
                leadingIcon = if (activo) {
                    { Icon(Icons.Filled.Check, contentDescription = null) }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun ListaPosts(
    posts: List<Post>,
    usuarioFiltro: Int?,
    onAbrirPost: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            val origen = if (usuarioFiltro == null) "de toda la comunidad" else "del Usuario $usuarioFiltro"
            Text(
                text = "${posts.size} publicaciones $origen",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }
        items(posts, key = { it.id }) { post ->
            TarjetaPost(
                userId = post.userId,
                titulo = post.title,
                cuerpo = post.body,
                onClick = { onAbrirPost(post.id) },
                maxLineas = 2
            )
        }
    }
}
