package com.abad.pokedex.ui.lista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.components.Cargando
import com.abad.pokedex.ui.components.ErrorView

@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel,
    onPokemonClick: (String) -> Unit
) {
    val tipos = listOf(
        "Fuego" to "fire",
        "Agua" to "water",
        "Planta" to "grass"
    )

    var tipoSeleccionado by remember {
        mutableStateOf("fire")
    }

    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(tipoSeleccionado) {
        viewModel.cargar(tipoSeleccionado)
    }

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

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tipos.forEach { (nombre, valor) ->
                FilterChip(
                    selected = tipoSeleccionado == valor,
                    onClick = {
                        tipoSeleccionado = valor
                    },
                    label = { Text(nombre) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when (val actual = estado) {
            UiState.Cargando -> Cargando()

            is UiState.Error -> ErrorView(
                mensaje = actual.mensaje,
                onReintentar = {
                    viewModel.cargar(tipoSeleccionado)
                }
            )

            is UiState.Exito -> {
                if (actual.datos.isEmpty()) {
                    Text("No se encontraron Pokémon")
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = pokemon.nombre.replaceFirstChar {
                    it.uppercase()
                },
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Tipo: ${pokemon.tipos.joinToString()}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
