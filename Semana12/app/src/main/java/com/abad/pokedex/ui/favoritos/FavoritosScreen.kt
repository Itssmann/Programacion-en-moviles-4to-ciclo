package com.abad.pokedex.ui.favoritos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.pokedex.ui.components.EtiquetaTipo

@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    onPokemonClick: (String) -> Unit,
    onVolver: () -> Unit
) {
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()

    var filtro by rememberSaveable {
        mutableStateOf("todos")
    }

    val opciones = listOf(
        "Todos" to "todos",
        "Fuego" to "fire",
        "Agua" to "water",
        "Planta" to "grass"
    )

    val filtrados = if (filtro == "todos") {
        favoritos
    } else {
        favoritos.filter { pokemon ->
            pokemon.tipos.any {
                it.equals(filtro, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Mis favoritos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Organiza tu colección de Pokémon.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Pokémon favoritos",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "${favoritos.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            opciones.forEach { (etiqueta, valor) ->
                val color = when (valor) {
                    "fire" -> Color(0xFFE76F51)
                    "water" -> Color(0xFF478BD3)
                    "grass" -> Color(0xFF4BA879)
                    else -> MaterialTheme.colorScheme.secondary
                }

                FilterChip(
                    selected = filtro == valor,
                    onClick = { filtro = valor },
                    label = { Text(etiqueta) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor =
                            color.copy(alpha = 0.15f),
                        selectedLabelColor = color
                    )
                )
            }
        }

        if (filtrados.isEmpty()) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    )

                    Text(
                        if (favoritos.isEmpty()) {
                            "Aún no tienes favoritos"
                        } else {
                            "Sin favoritos de este tipo"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        if (favoritos.isEmpty()) {
                            "Explora el catálogo y guarda tus Pokémon preferidos."
                        } else {
                            "Prueba otro filtro para ver tu colección."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    filtrados,
                    key = { it.nombre }
                ) { pokemon ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPokemonClick(pokemon.nombre)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    pokemon.nombre.replaceFirstChar {
                                        it.uppercase()
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    pokemon.tipos.forEach { tipo ->
                                        EtiquetaTipo(tipo)
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    viewModel.alternarFavorito(
                                        pokemon.nombre
                                    )
                                }
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Quitar favorito",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
