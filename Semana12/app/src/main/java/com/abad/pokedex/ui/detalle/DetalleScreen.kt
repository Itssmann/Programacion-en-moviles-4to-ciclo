package com.abad.pokedex.ui.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.components.Cargando
import com.abad.pokedex.ui.components.ErrorView
import java.util.Locale

@Composable
fun DetalleScreen(
    nombre: String,
    viewModel: DetalleViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(nombre) {
        viewModel.cargarDetalle(nombre)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(
            onClick = onVolver,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("← Volver al catálogo")
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
                ContenidoDetalle(actual.datos)
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(pokemon: Pokemon) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = pokemon.nombre.replaceFirstChar {
                    it.uppercase()
                },
                style = MaterialTheme.typography.headlineLarge
            )
        }

        item {
            Text(
                text = "Tipos: ${pokemon.tipos.joinToString()}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Información general",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Altura: ${
                            String.format(
                                Locale.US,
                                "%.1f",
                                pokemon.altura / 10.0
                            )
                        } m"
                    )

                    Text(
                        "Peso: ${
                            String.format(
                                Locale.US,
                                "%.1f",
                                pokemon.peso / 10.0
                            )
                        } kg"
                    )
                }
            }
        }

        item {
            Text(
                "Habilidades",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(pokemon.habilidades) { habilidad ->
            Text("• ${habilidad.replace("-", " ")}")
        }

        item {
            Text(
                "Estadísticas",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(pokemon.estadisticas.toList()) { (nombre, valor) ->
            Column {
                Text(
                    "${nombre.replace("-", " ")}: $valor"
                )

                LinearProgressIndicator(
                    progress = { (valor / 255f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
