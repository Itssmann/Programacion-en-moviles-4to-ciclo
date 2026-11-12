package com.abad.pokedex.ui.favoritos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.components.EtiquetaTipo
import com.abad.pokedex.ui.components.PokemonImage

private fun colorPorTipo(tipo: String): Color {
    return when (tipo.lowercase()) {
        "fire" -> Color(0xFFE76F51)
        "water" -> Color(0xFF478BD3)
        "grass" -> Color(0xFF4BA879)
        else -> Color(0xFF7A889B)
    }
}

private fun obtenerNumeroPokedex(imagenUrl: String?): Int {
    if (imagenUrl.isNullOrBlank()) return 0
    val regex = "/pokemon/(\\d+)\\.png".toRegex()
    val match = regex.find(imagenUrl)
    return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
}

@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    onPokemonClick: (String) -> Unit,
    onVolver: () -> Unit
) {
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()

    var filtro by rememberSaveable { mutableStateOf("todos") }

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
            pokemon.tipos.any { it.equals(filtro, ignoreCase = true) }
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
            text = "Tu colección personal de Pokémon guardados",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Tarjeta resumen
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Favoritos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${favoritos.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Filtros por tipo
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { (etiqueta, valor) ->
                val color = if (valor == "todos") MaterialTheme.colorScheme.primary else colorPorTipo(valor)

                FilterChip(
                    selected = filtro == valor,
                    onClick = { filtro = valor },
                    label = { Text(etiqueta) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color.copy(alpha = 0.18f),
                        selectedLabelColor = color
                    )
                )
            }
        }

        if (filtrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (favoritos.isEmpty()) Icons.Default.FavoriteBorder else Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (favoritos.isEmpty()) "Aún no tienes favoritos" else "Sin favoritos de este tipo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (favoritos.isEmpty()) "Explora el catálogo y marca tus Pokémon preferidos." else "Prueba seleccionando otro filtro.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    filtrados,
                    key = { it.nombre }
                ) { pokemon ->
                    FavoritoCardModern(
                        pokemon = pokemon,
                        onClick = { onPokemonClick(pokemon.nombre) },
                        onFavoritoClick = { viewModel.alternarFavorito(pokemon.nombre) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritoCardModern(
    pokemon: Pokemon,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    val primerTipo = pokemon.tipos.firstOrNull() ?: "normal"
    val colorTipo = colorPorTipo(primerTipo)
    val numeroId = obtenerNumeroPokedex(pokemon.imagenUrl)
    val numeroFormateado = if (numeroId > 0) "#${numeroId.toString().padStart(3, '0')}" else "#---"

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
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
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorTipo.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                PokemonImage(
                    url = pokemon.imagenUrl,
                    contentDescription = pokemon.nombre,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = numeroFormateado,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorTipo
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = pokemon.nombre.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pokemon.tipos.forEach { tipo ->
                        EtiquetaTipo(tipo)
                    }
                }
            }

            IconButton(
                onClick = onFavoritoClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Quitar de favoritos",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
