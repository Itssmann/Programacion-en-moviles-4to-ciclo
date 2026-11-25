package com.abad.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MuroCampus") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrear,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva publicación")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FiltroUsuarios(
                seleccionado = usuarioFiltro,
                onSeleccionar = { vm.filtrar(it) }
            )
            when (val e = estado) {
                is UiState.Cargando -> Cargando("Cargando publicaciones...")
                is UiState.Error -> ErrorConReintento(
                    mensaje = e.mensaje,
                    onReintentar = { vm.cargar() }
                )
                is UiState.Exito ->
                    if (e.datos.isEmpty()) SinPublicaciones()
                    else ListaPosts(e.datos, onAbrirPost)
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
            FilterChip(
                selected = seleccionado == userId,
                onClick = { onSeleccionar(userId) },
                label = { Text(if (userId == null) "Todos" else "Usuario $userId") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun SinPublicaciones() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sin publicaciones", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ListaPosts(
    posts: List<Post>,
    onAbrirPost: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
