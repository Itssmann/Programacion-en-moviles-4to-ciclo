package com.abad.pokedex.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.abad.pokedex.ui.detalle.DetalleScreen
import com.abad.pokedex.ui.detalle.DetalleViewModel
import com.abad.pokedex.ui.favoritos.FavoritosScreen
import com.abad.pokedex.ui.favoritos.FavoritosViewModel
import com.abad.pokedex.ui.inicio.InicioScreen
import com.abad.pokedex.ui.lista.PokemonListScreen
import com.abad.pokedex.ui.lista.PokemonViewModel
import com.abad.pokedex.ui.perfil.PerfilScreen

@Composable
fun AppNavigation(
    pokemonViewModel: PokemonViewModel,
    detalleViewModel: DetalleViewModel,
    favoritosViewModel: FavoritosViewModel
) {
    var pantalla by rememberSaveable {
        mutableStateOf("inicio")
    }

    var pantallaAnterior by rememberSaveable {
        mutableStateOf("catalogo")
    }

    var pokemonSeleccionado by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    BackHandler(enabled = pantalla == "detalle") {
        pantalla = pantallaAnterior
        pokemonSeleccionado = null
    }

    Scaffold(
        bottomBar = {
            if (pantalla != "detalle") {
                NavigationBar {
                    val opciones = listOf(
                        Triple("inicio", "Inicio", Icons.Default.Home),
                        Triple("catalogo", "Pokémon", Icons.Default.List),
                        Triple("favoritos", "Favoritos", Icons.Default.Favorite),
                        Triple("perfil", "Perfil", Icons.Default.Person)
                    )

                    opciones.forEach { (ruta, titulo, icono) ->
                        NavigationBarItem(
                            selected = pantalla == ruta,
                            onClick = {
                                pantalla = ruta
                            },
                            icon = {
                                Icon(
                                    imageVector = icono,
                                    contentDescription = titulo
                                )
                            },
                            label = {
                                Text(titulo)
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(padding)
        ) {
            when (pantalla) {
                "inicio" -> InicioScreen(
                    pokemonViewModel = pokemonViewModel,
                    favoritosViewModel = favoritosViewModel,
                    onVerPokemones = {
                        pantalla = "catalogo"
                    },
                    onVerFavoritos = {
                        pantalla = "favoritos"
                    }
                )

                "catalogo" -> PokemonListScreen(
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

                "favoritos" -> FavoritosScreen(
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

                "perfil" -> PerfilScreen()

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
            }
        }
    }
}
