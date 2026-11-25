package com.abad.murocampus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import com.abad.murocampus.ui.theme.colorEncabezado
import com.abad.murocampus.ui.theme.degradadoBajoBarra
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.murocampus.model.Comentario
import com.abad.murocampus.model.Post
import com.abad.murocampus.viewmodel.DetalleUi
import com.abad.murocampus.viewmodel.PostsViewModel
import com.abad.murocampus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    vm: PostsViewModel,
    idPost: Int,
    onVolver: () -> Unit
) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val borrando by vm.borrando.collectAsStateWithLifecycle()
    val postBorrado by vm.postBorrado.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var confirmarBorrado by rememberSaveable { mutableStateOf(false) }
    var mostrarEdicion by rememberSaveable { mutableStateOf(false) }
    val editando by vm.editando.collectAsStateWithLifecycle()
    val edicionTerminada by vm.edicionTerminada.collectAsStateWithLifecycle()

    LaunchedEffect(edicionTerminada) {
        if (edicionTerminada) {
            mostrarEdicion = false
            vm.edicionAtendida()
        }
    }

    val detalleActual = estado
    if (mostrarEdicion && detalleActual is UiState.Exito) {
        HojaEditarTitulo(
            tituloActual = detalleActual.datos.post.title,
            guardando = editando,
            onGuardar = { vm.editarTitulo(idPost, it) },
            onCerrar = { mostrarEdicion = false }
        )
    }

    BackHandler { onVolver() }

    LaunchedEffect(postBorrado) {
        if (postBorrado == idPost) {
            vm.borradoAtendido()
            onVolver()
        }
    }

    if (confirmarBorrado) {
        DialogoBorrar(
            idPost = idPost,
            onConfirmar = {
                confirmarBorrado = false
                vm.borrar(idPost)
            },
            onCancelar = { confirmarBorrado = false }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { AvisosSnackbar(vm) },
        topBar = {
            TopAppBar(
                title = { Text("Publicación #$idPost") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (borrando) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else if (estado is UiState.Exito) {
                        IconButton(onClick = { mostrarEdicion = true }) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Editar título")
                        }
                        IconButton(onClick = { confirmarBorrado = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Eliminar publicación",
                                tint = Color.White
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorEncabezado(),
                    scrolledContainerColor = colorEncabezado(),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (val e = estado) {
            is UiState.Cargando -> Cargando("Cargando publicación...", modifier)
            is UiState.Error -> ErrorConReintento(
                mensaje = e.mensaje,
                onReintentar = { vm.abrir(idPost) },
                modifier = modifier
            )
            is UiState.Exito -> ContenidoDetalle(e.datos, modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HojaEditarTitulo(
    tituloActual: String,
    guardando: Boolean,
    onGuardar: (String) -> Unit,
    onCerrar: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(tituloActual) }
    val cambio = titulo.isNotBlank() && titulo.trim() != tituloActual

    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Editar título", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Solo se envía el título con PATCH; el contenido no cambia.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Nuevo título") },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCerrar) { Text("Cancelar") }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { onGuardar(titulo.trim()) },
                    enabled = cambio && !guardando
                ) {
                    if (guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (guardando) "Guardando..." else "Guardar")
                }
            }
        }
    }
}

@Composable
private fun DialogoBorrar(idPost: Int, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
        title = { Text("¿Eliminar publicación?") },
        text = {
            Text("La publicación #$idPost y sus comentarios dejarán de verse en tu muro.")
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
private fun ContenidoDetalle(detalle: DetalleUi, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { CabeceraPost(detalle.post) }
        item { CuerpoPost(detalle.post) }
        item { TituloComentarios(detalle.comentarios.size) }
        items(detalle.comentarios, key = { it.id }) { comentario ->
            Box(Modifier.padding(horizontal = 16.dp)) { TarjetaComentario(comentario) }
        }
    }
}

@Composable
private fun CabeceraPost(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = degradadoBajoBarra(),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AvatarUsuario(post.userId, tamano = 48.dp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = nombreDe(post.userId),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "Publicó en el muro del campus",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = post.title.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
    }
}

@Composable
private fun CuerpoPost(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = post.body.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun TituloComentarios(cantidad: Int) {
    Row(
        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.Comment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Text("Comentarios", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(8.dp))
        Badge(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text("$cantidad", modifier = Modifier.padding(horizontal = 4.dp))
        }
    }
}

@Composable
private fun TarjetaComentario(comentario: Comentario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comentario.email.first().uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = comentario.email,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = comentario.body,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
