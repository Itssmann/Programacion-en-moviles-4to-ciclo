package com.abad.pokedex.ui.lista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.components.Cargando
import com.abad.pokedex.ui.components.ErrorView

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
            .padding(16.dp)
    ) {
        Text(
            text = "Pokédex Abad",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))
        Text("Explora Pokémon de fuego, agua y planta")
        Spacer(Modifier.height(12.dp))

        Button(onClick = onFavoritosClick) {
            Text("♥ Mis favoritos")
        }

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tipos.forEach { (nombre, valor) ->
                FilterChip(
                    selected = tipoSeleccionado == valor,
                    onClick = {
                        viewModel.cargar(valor)
                    },
                    label = { Text(nombre) }
                )
            }
        }

        OutlinedButton(
            onClick = { viewModel.refrescar() },
            enabled = !refrescando
        ) {
            Text(if (refrescando) "Actualizando…" else "Actualizar")
        }
        aviso?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (refrescando && estado is UiState.Exito) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(16.dp))

        when (val actual = estado) {
            UiState.Cargando -> Cargando()

            is UiState.Error -> ErrorView(
                mensaje = actual.mensaje,
                onReintentar = {
                    viewModel.refrescar()
                }
            )

            is UiState.Exito -> {
                if (actual.datos.isEmpty()) {
                    Text("No hay Pokémon disponibles de este tipo.")
                } else {
                    LazyColumn(
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
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
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = pokemon.nombre.replaceFirstChar {
                        it.uppercase()
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Tipo: ${pokemon.tipos.joinToString()}"
                )
            }

            TextButton(onClick = onFavoritoClick) {
                Text(
                    if (pokemon.esFavorito) "♥" else "♡",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
