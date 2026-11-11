package com.abad.pokedex.ui.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.components.Cargando
import com.abad.pokedex.ui.components.ErrorView
import java.util.Locale
import com.abad.pokedex.ui.components.EtiquetaTipo

private fun colorPorTipo(tipo: String): Color {
    return when (tipo.lowercase()) {
        "fire" -> Color(0xFFE76F51)
        "water" -> Color(0xFF478BD3)
        "grass" -> Color(0xFF4BA879)
        else -> Color(0xFF5266A0)
    }
}

private fun nombreEstadistica(nombre: String): String {
    return when (nombre.lowercase()) {
        "hp" -> "PS"
        "attack" -> "Ataque"
        "defense" -> "Defensa"
        "special-attack" -> "Ataque especial"
        "special-defense" -> "Defensa especial"
        "speed" -> "Velocidad"
        else -> nombre.replace("-", " ")
            .replaceFirstChar { it.uppercase() }
    }
}

@Composable
fun DetalleScreen(
    nombre: String,
    viewModel: DetalleViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    LaunchedEffect(nombre) {
        viewModel.cargarDetalle(nombre)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextButton(
            onClick = onVolver,
            modifier = Modifier.padding(
                start = 12.dp,
                top = 8.dp
            ),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Volver")
        }

        when (val actual = estado) {
            UiState.Cargando -> Cargando()

            is UiState.Error -> ErrorView(
                mensaje = actual.mensaje,
                onReintentar = {
                    viewModel.cargarDetalle(nombre)
                }
            )

            is UiState.Exito -> {
                ContenidoDetalle(
                    pokemon = actual.datos,
                    onFavoritoClick = {
                        viewModel.alternarFavorito()
                    }
                )
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(
    pokemon: Pokemon,
    onFavoritoClick: () -> Unit
) {
    val colorTipo = colorPorTipo(
        pokemon.tipos.firstOrNull().orEmpty()
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pokemon.nombre.replaceFirstChar {
                        it.uppercase()
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onFavoritoClick) {
                    Icon(
                        imageVector = if (pokemon.esFavorito) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = if (pokemon.esFavorito) {
                            "Quitar de favoritos"
                        } else {
                            "Agregar a favoritos"
                        },
                        tint = if (pokemon.esFavorito) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    )
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pokemon.tipos.forEach { tipo ->
                    EtiquetaTipo(tipo)
                }
            }
        }

        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Información general",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Altura: ${
                            String.format(
                                Locale.US,
                                "%.1f",
                                pokemon.altura / 10.0
                            )
                        } m",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        "Peso: ${
                            String.format(
                                Locale.US,
                                "%.1f",
                                pokemon.peso / 10.0
                            )
                        } kg",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Text(
                "Habilidades",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(pokemon.habilidades) { habilidad ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = habilidad.replace("-", " ")
                        .replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Text(
                "Estadísticas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(pokemon.estadisticas.toList()) { (nombre, valor) ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            nombreEstadistica(nombre),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            valor.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorTipo
                        )
                    }

                    LinearProgressIndicator(
                        progress = {
                            (valor / 255f).coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = colorTipo,
                        trackColor = colorTipo.copy(alpha = 0.15f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        drawStopIndicator = {}
                    )
                }
            }
        }
    }
}
