package com.abad.pokedex.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.abad.pokedex.ui.detalle.DetalleScreen
import com.abad.pokedex.ui.detalle.DetalleViewModel
import com.abad.pokedex.ui.lista.PokemonListScreen
import com.abad.pokedex.ui.lista.PokemonViewModel

@Composable
fun AppNavigation(
    pokemonViewModel: PokemonViewModel,
    detalleViewModel: DetalleViewModel
) {
    var pokemonSeleccionado by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val nombre = pokemonSeleccionado

    if (nombre == null) {
        PokemonListScreen(
            viewModel = pokemonViewModel,
            onPokemonClick = { pokemon ->
                pokemonSeleccionado = pokemon
            }
        )
    } else {
        DetalleScreen(
            nombre = nombre,
            viewModel = detalleViewModel,
            onVolver = {
                pokemonSeleccionado = null
            }
        )
    }
}
