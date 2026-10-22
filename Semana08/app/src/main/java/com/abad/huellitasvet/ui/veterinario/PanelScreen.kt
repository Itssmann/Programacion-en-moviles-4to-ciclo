package com.abad.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.huellitasvet.viewmodel.PanelViewModel
import com.abad.huellitasvet.ui.components.graficos.GraficoDona
import com.abad.huellitasvet.ui.components.graficos.GraficoBarras
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PanelScreen(
    viewModel: PanelViewModel,
    onAgenda: () -> Unit,
    bottomBar: @Composable () -> Unit = {}
) {
    val citasHoy by viewModel.citasHoy.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()
    val atendidas by viewModel.atendidas.collectAsState()
    val ingresos by viewModel.ingresosMes.collectAsState()
    val estados by viewModel.porEstado.collectAsState()
    val servicios by viewModel.porServicio.collectAsState()

    val fechaHoy = remember {
        val formato = DateTimeFormatter.ofPattern("EEE dd MMM", Locale.forLanguageTag("es"))
        val texto = LocalDate.now().format(formato)
        "Hoy · " + texto.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("es")) else it.toString() }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Panel de la clínica",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        fechaHoy,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    IconButton(onClick = {}) {
                        Text("🔔", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Three metric cards
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Citas hoy (light blue)
                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFFDBEAFE)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$citasHoy",
                            color = Color(0xFF1D4ED8),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Citas hoy",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1E40AF),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Pendientes (light yellow)
                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFFFEF3C7)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$pendientes",
                            color = Color(0xFFB45309),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Pendientes",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Atendidas (light gray/blue-gray)
                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFFE2E8F0)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$atendidas",
                            color = Color(0xFF334155),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Atendidas",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Ingresos del mes card (compact blue card with atenciones count)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Ingresos del mes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            "S/ ${"%.2f".format(ingresos)}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "$atendidas atenciones",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                "Citas por estado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            GraficoDona(
                datos = estados,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Citas por servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            GraficoBarras(
                datos = servicios.map { conteo ->
                    conteo.nombre to conteo.cantidad
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Notice card below charts (yellow compact card)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFFFEF3C7)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⚠️", style = MaterialTheme.typography.titleLarge)
                        Column {
                            Text(
                                if (pendientes > 0) "$pendientes citas por confirmar" else "No hay citas pendientes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                            Text(
                                "para hoy y mañana",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                    Button(
                        onClick = onAgenda,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD97706),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text("Ver", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
