package com.abad.pokedex.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.abad.pokedex.ui.detalle.DetalleScreen
import com.abad.pokedex.ui.detalle.DetalleViewModel
import com.abad.pokedex.ui.favoritos.FavoritosScreen
import com.abad.pokedex.ui.favoritos.FavoritosViewModel
import com.abad.pokedex.ui.lista.PokemonListScreen
import com.abad.pokedex.ui.lista.PokemonViewModel

@Composable
fun AppNavigation(
    pokemonViewModel: PokemonViewModel,
    detalleViewModel: DetalleViewModel,
    favoritosViewModel: FavoritosViewModel
) {
    var pantalla by rememberSaveable {
        mutableStateOf("catalogo")
    }

    var pokemonSeleccionado by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var pantallaAnterior by rememberSaveable {
        mutableStateOf("catalogo")
    }

    when (pantalla) {
        "favoritos" -> {
            FavoritosScreen(
                viewModel = favoritosViewModel,
                onPokemonClick = { nombre ->
                    pokemonSeleccionado = nombre
                    pantallaAnterior = "favoritos"
                    pantalla = "detalle"
                },
                onVolver = {
                    pantalla = "catalogo"
                }
            )
        }

        "detalle" -> {
            val nombre = pokemonSeleccionado

            if (nombre != null) {
                DetalleScreen(
                    nombre = nombre,
                    viewModel = detalleViewModel,
                    onVolver = {
                        pantalla = pantallaAnterior
                        pokemonSeleccionado = null
                    }
                )
            }
        }

        else -> {
            PokemonListScreen(
                viewModel = pokemonViewModel,
                onPokemonClick = { nombre ->
                    pokemonSeleccionado = nombre
                    pantallaAnterior = "catalogo"
                    pantalla = "detalle"
                },
                onFavoritosClick = {
                    pantalla = "favoritos"
                }
            )
        }
    }
}
