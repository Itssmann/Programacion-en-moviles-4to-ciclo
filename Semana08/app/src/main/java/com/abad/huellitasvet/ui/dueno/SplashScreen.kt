package com.abad.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import com.abad.huellitasvet.ui.navigation.Rutas

@Composable
fun SplashScreen(
    preferencias: PreferenciasStore,
    onDestino: (String) -> Unit
) {
    val perfilActivo by preferencias.perfilActivo.collectAsState(initial = null)
    val usuarioId by preferencias.usuarioId.collectAsState(initial = null)

    LaunchedEffect(perfilActivo, usuarioId) {
        if (perfilActivo != null && usuarioId != null) {
            val destino = when {
                usuarioId!! <= 0 -> null // Muestra la pantalla interactiva de bienvenida
                perfilActivo == "CLIENTE" -> Rutas.INICIO
                perfilActivo == "ADMIN" -> Rutas.PANEL
                else -> null
            }
            if (destino != null) {
                onDestino(destino)
            }
        }
    }

    if (perfilActivo == null || usuarioId == null || usuarioId!! <= 0) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF6FDF9)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF059669)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "HuellitasVet",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Cuidamos a tu mejor amigo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(48.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("🐶", "🐱", "🐰").forEach { emoji ->
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFD1FAE5)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emoji, fontSize = 24.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(64.dp))

                Button(
                    onClick = { onDestino(Rutas.LOGIN) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF059669)
                    )
                ) {
                    Text(
                        "Comenzar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = { onDestino(Rutas.LOGIN) }
                ) {
                    Text(
                        "Ya tengo una cuenta",
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF059669))
        }
    }
}
