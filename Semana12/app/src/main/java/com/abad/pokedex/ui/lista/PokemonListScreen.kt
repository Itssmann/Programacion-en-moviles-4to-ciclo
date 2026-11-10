package com.abad.pokedex.ui.lista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.pokedex.domain.model.Pokemon

@Composable
fun PokemonListScreen() {

    val tipos = listOf("Fuego", "Agua", "Planta")
    var tipoSeleccionado by remember {
        mutableStateOf("Fuego")
    }

    val pokemones = listOf(
        Pokemon(
            nombre = "Charmander",
            imagenUrl = null,
            tipos = listOf("Fuego")
        ),
        Pokemon(
            nombre = "Vulpix",
            imagenUrl = null,
            tipos = listOf("Fuego")
        ),
        Pokemon(
            nombre = "Squirtle",
            imagenUrl = null,
            tipos = listOf("Agua")
        ),
        Pokemon(
            nombre = "Psyduck",
            imagenUrl = null,
            tipos = listOf("Agua")
        ),
        Pokemon(
            nombre = "Bulbasaur",
            imagenUrl = null,
            tipos = listOf("Planta")
        ),
        Pokemon(
            nombre = "Oddish",
            imagenUrl = null,
            tipos = listOf("Planta")
        )
    )

    val filtrados = pokemones.filter {
        tipoSeleccionado in it.tipos
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

        Text(
            text = "Explora Pokémon de fuego, agua y planta",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tipos.forEach { tipo ->
                FilterChip(
                    selected = tipoSeleccionado == tipo,
                    onClick = { tipoSeleccionado = tipo },
                    label = { Text(tipo) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtrados) { pokemon ->
                PokemonCard(pokemon)
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
                text = pokemon.nombre,
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
