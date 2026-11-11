package com.abad.pokedex.ui.favoritos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    onPokemonClick: (String) -> Unit,
    onVolver: () -> Unit
) {
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onVolver) {
            Text("← Volver al catálogo")
        }

        Text(
            text = "Mis Pokémon favoritos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        if (favoritos.isEmpty()) {
            Text("Todavía no tienes Pokémon favoritos.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    favoritos,
                    key = { it.nombre }
                ) { pokemon ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPokemonClick(pokemon.nombre)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    pokemon.nombre.replaceFirstChar {
                                        it.uppercase()
                                    },
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    "Tipos: ${pokemon.tipos.joinToString()}"
                                )
                            }

                            TextButton(
                                onClick = {
                                    viewModel.alternarFavorito(
                                        pokemon.nombre
                                    )
                                }
                            ) {
                                Text("♥ Quitar")
                            }
                        }
                    }
                }
            }
        }
    }
}
