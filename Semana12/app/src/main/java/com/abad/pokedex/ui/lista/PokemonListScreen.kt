package com.abad.pokedex.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.components.Cargando
import com.abad.pokedex.ui.components.ErrorView

@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel
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
                    onClick = { tipoSeleccionado = valor },
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
                            PokemonCard(pokemon)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonCard(pokemon: Pokemon) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
