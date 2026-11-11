package com.abad.pokedex.ui.lista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel,
    onPokemonClick: (String) -> Unit,
    onFavoritosClick: () -> Unit
) {
    val tipos = listOf(
        "Fuego" to "fire",
        "Agua" to "water",
        "Planta" to "grass"
    )

    val tipoSeleccionado by viewModel.tipoActual.collectAsStateWithLifecycle()
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val refrescando by viewModel.refrescando.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Explorar Pokémon",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Descubre Pokémon de fuego, agua y planta.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Selecciona un tipo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tipos.forEach { (nombre, valor) ->
                val color = when (valor) {
                    "fire" -> Color(0xFFE76F51)
                    "water" -> Color(0xFF478BD3)
                    else -> Color(0xFF4BA879)
                }

                FilterChip(
                    selected = tipoSeleccionado == valor,
                    onClick = { viewModel.cargar(valor) },
                    label = { Text(nombre) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color.copy(alpha = 0.16f),
                        selectedLabelColor = color
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { viewModel.refrescar() },
            enabled = !refrescando,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                if (refrescando) "Actualizando..." else "Actualizar"
            )
        }

        aviso?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (refrescando && estado is UiState.Exito) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        when (val actual = estado) {
            UiState.Cargando -> Cargando()

            is UiState.Error -> ErrorView(
                mensaje = actual.mensaje,
                onReintentar = { viewModel.refrescar() }
            )

            is UiState.Exito -> {
                if (actual.datos.isEmpty()) {
                    Text(
                        "No hay Pokémon disponibles de este tipo.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(
                            actual.datos,
                            key = { it.nombre }
                        ) { pokemon ->
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = {
                                    onPokemonClick(pokemon.nombre)
                                },
                                onFavoritoClick = {
                                    viewModel.alternarFavorito(
                                        pokemon.nombre
                                    )
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
private fun PokemonCard(
    pokemon: Pokemon,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
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
            PokemonImage(
                url = pokemon.imagenUrl,
                contentDescription = pokemon.nombre,
                modifier = Modifier
                    .size(60.dp)
                    .clickable(onClick = onClick)
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = pokemon.nombre.replaceFirstChar {
                        it.uppercase()
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {
                    pokemon.tipos.forEach { tipo ->
                        EtiquetaTipo(tipo)
                    }
                }
            }

            IconButton(onClick = onFavoritoClick) {
                Icon(
                    imageVector = if (pokemon.esFavorito) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (pokemon.esFavorito) {
                        "Quitar de favoritos"
                    } else {
                        "Agregar a favoritos"
                    },
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
