package com.abad.pokedex.ui.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.components.Cargando
import com.abad.pokedex.ui.components.ErrorView
import com.abad.pokedex.ui.components.EtiquetaTipo
import com.abad.pokedex.ui.components.PokemonImage

private fun obtenerNumeroPokedex(imagenUrl: String?): Int {
    if (imagenUrl.isNullOrBlank()) return 0
    val regex = "/pokemon/(\\d+)\\.png".toRegex()
    val match = regex.find(imagenUrl)
    return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
}

private fun colorPorTipo(tipo: String): Color {
    return when (tipo.lowercase()) {
        "fire" -> Color(0xFFE76F51)
        "water" -> Color(0xFF478BD3)
        "grass" -> Color(0xFF4BA879)
        else -> Color(0xFF7A889B)
    }
}

@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel,
    onPokemonClick: (String) -> Unit,
    onFavoritosClick: () -> Unit
) {
    val tipos = listOf(
        "Todos" to "all",
        "Fuego" to "fire",
        "Agua" to "water",
        "Planta" to "grass"
    )

    val ordenes = listOf("Número", "Nombre")

    val tipoSeleccionado by viewModel.tipoSeleccionado.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val ordenSeleccionado by viewModel.ordenSeleccionado.collectAsStateWithLifecycle()
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val refrescando by viewModel.refrescando.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        // Título y Subtítulo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pokédex Abad",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Explora y descubre tus Pokémon favoritos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { viewModel.refrescar() },
                enabled = !refrescando,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Actualizar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Barra de búsqueda moderna
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar Pokémon por nombre...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )

        Spacer(Modifier.height(14.dp))

        // Filtros por Tipo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tipos.forEach { (nombre, valor) ->
                val color = if (valor == "all") MaterialTheme.colorScheme.primary else colorPorTipo(valor)

                FilterChip(
                    selected = tipoSeleccionado == valor,
                    onClick = { viewModel.setTipo(valor) },
                    label = { Text(nombre) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color.copy(alpha = 0.18f),
                        selectedLabelColor = color
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Selector de Ordenamiento
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Sort,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Ordenar por: ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ordenes.forEach { orden ->
                val seleccionado = (ordenSeleccionado == orden || (ordenSeleccionado == "Numero" && orden == "Número"))
                TextButton(
                    onClick = { viewModel.setOrden(if (orden == "Número") "Numero" else "Nombre") },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = orden,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                        color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        aviso?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (refrescando && estado is UiState.Exito) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Contenido principal
        when (val actual = estado) {
            UiState.Cargando -> Cargando()

            is UiState.Error -> ErrorView(
                mensaje = actual.mensaje,
                onReintentar = { viewModel.refrescar() }
            )

            is UiState.Exito -> {
                if (actual.datos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "No se encontraron Pokémon",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Prueba con otro nombre o filtro de tipo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            actual.datos,
                            key = { it.nombre }
                        ) { pokemon ->
                            PokemonCardModern(
                                pokemon = pokemon,
                                onClick = { onPokemonClick(pokemon.nombre) },
                                onFavoritoClick = { viewModel.alternarFavorito(pokemon.nombre) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonCardModern(
    pokemon: Pokemon,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    val primerTipo = pokemon.tipos.firstOrNull() ?: "normal"
    val colorTipo = colorPorTipo(primerTipo)
    val numeroId = obtenerNumeroPokedex(pokemon.imagenUrl)
    val numeroFormateado = if (numeroId > 0) "#${numeroId.toString().padStart(3, '0')}" else "#---"

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor de imagen con fondo suave del tipo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorTipo.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                PokemonImage(
                    url = pokemon.imagenUrl,
                    contentDescription = pokemon.nombre,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Información (Número, Nombre, Tipos)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = numeroFormateado,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorTipo
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = pokemon.nombre.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pokemon.tipos.forEach { tipo ->
                        EtiquetaTipo(tipo)
                    }
                }
            }

            // Botón de Favorito
            IconButton(
                onClick = onFavoritoClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (pokemon.esFavorito) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (pokemon.esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (pokemon.esFavorito) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
    }
}
