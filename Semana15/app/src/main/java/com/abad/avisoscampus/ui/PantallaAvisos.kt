package com.abad.avisoscampus.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.abad.avisoscampus.R
import com.abad.avisoscampus.model.PermisoUi
import com.abad.avisoscampus.notificacion.Notificador
import com.abad.avisoscampus.receiver.CargadorReceiver
import com.abad.avisoscampus.receiver.ModoAvionReceiver
import com.abad.avisoscampus.ui.theme.AmbarRecordatorio
import com.abad.avisoscampus.ui.theme.AmbarSuave
import com.abad.avisoscampus.ui.theme.AzulInstitucional
import com.abad.avisoscampus.ui.theme.CelesteSuave
import com.abad.avisoscampus.ui.theme.CoralError
import com.abad.avisoscampus.ui.theme.CoralSuave
import com.abad.avisoscampus.ui.theme.GrisAzulado
import com.abad.avisoscampus.ui.theme.GrisSuave
import com.abad.avisoscampus.ui.theme.Turquesa
import com.abad.avisoscampus.ui.theme.TurquesaSuave
import com.abad.avisoscampus.ui.theme.VerdeExito
import com.abad.avisoscampus.ui.theme.VerdeSuave
import com.abad.avisoscampus.viewmodel.AvisosViewModel

private const val SEGUNDOS_RECORDATORIO = 10

@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val faltan by vm.faltan.collectAsState()
    val corriendo = faltan != null
    val escuchando by vm.escuchandoAvion.collectAsState()
    val escuchandoCargador by vm.escuchandoCargador.collectAsState()
    val avisos by vm.avisos.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    LaunchedEffect(Unit) {
        // Si el permiso ya estaba concedido, se lo avisamos al ViewModel
        if (Notificador.tienePermiso(contexto)) vm.permisoRespondido(true)
    }

    DisposableEffect(escuchando) {
        if (escuchando) {
            val receptor = ModoAvionReceiver { activo -> vm.alCambiarModoAvion(activo) }
            ContextCompat.registerReceiver(
                contexto,
                receptor,
                IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            // Al salir (o al apagar el switch) se quita el receptor
            onDispose { contexto.unregisterReceiver(receptor) }
        } else {
            onDispose { }
        }
    }

    DisposableEffect(escuchandoCargador) {
        if (escuchandoCargador) {
            val receptor = CargadorReceiver { conectado -> vm.alCambiarCargador(conectado) }
            val filtro = IntentFilter().apply {
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            ContextCompat.registerReceiver(
                contexto,
                receptor,
                filtro,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose { contexto.unregisterReceiver(receptor) }
        } else {
            onDispose { }
        }
    }

    LaunchedEffect(Unit) {
        vm.eventos.collect { aviso ->
            val mostrado = Notificador.mostrar(contexto, aviso)
            if (!mostrado) vm.noSePudoMostrar()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { Encabezado(permiso = permiso, totalAvisos = avisos.size) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp) // en tablets no se estira de más
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TarjetaPermiso(
                    permiso = permiso,
                    mensaje = mensaje,
                    alPedir = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            vm.permisoRespondido(true)
                        }
                    },
                    alAbrirAjustes = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName)
                        contexto.startActivity(intent)
                    }
                )

                TarjetaSeccion(
                    titulo = "Avisos",
                    subtitulo = "Envía una notificación al instante",
                    icono = R.drawable.ic_notificacion
                ) {
                    Button(
                        onClick = { vm.avisarAhora() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_notificacion),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Mostrar aviso")
                    }
                }

                TarjetaRecordatorio(
                    faltan = faltan,
                    corriendo = corriendo,
                    alRecordar = { vm.recordarEn(SEGUNDOS_RECORDATORIO) },
                    alCancelar = { vm.cancelarRecordatorio() }
                )

                TarjetaSeccion(
                    titulo = "Receptor de anuncios",
                    subtitulo = "Escucha lo que pasa en el celular",
                    icono = R.drawable.ic_antena,
                    colorIcono = Turquesa,
                    fondoIcono = TurquesaSuave
                ) {
                    FilaInterruptor(
                        icono = R.drawable.ic_avion,
                        colorIcono = Turquesa,
                        fondoIcono = TurquesaSuave,
                        titulo = "Escuchar el modo avión",
                        descripcion = if (escuchando) "Escuchando cambios" else "Apagado",
                        activo = escuchando,
                        alCambiar = { vm.cambiarEscucha(it) }
                    )
                    FilaInterruptor(
                        icono = R.drawable.ic_rayo,
                        colorIcono = VerdeExito,
                        fondoIcono = VerdeSuave,
                        titulo = "Escuchar el cargador",
                        descripcion = if (escuchandoCargador) "Escuchando cambios" else "Apagado",
                        activo = escuchandoCargador,
                        alCambiar = { vm.cambiarEscuchaCargador(it) }
                    )
                }

                HistorialAvisos(avisos)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun Encabezado(permiso: PermisoUi, totalAvisos: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AzulInstitucional)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 18.dp)
    ) {
        Text(
            "AvisosCampus",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
        Text(
            "Tu campus, siempre conectado",
            style = MaterialTheme.typography.bodyMedium,
            color = CelesteSuave
        )
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val textoPermiso = when (permiso) {
                PermisoUi.Concedido -> "Notificaciones activas"
                PermisoUi.Denegado -> "Notificaciones bloqueadas"
                PermisoUi.SinPedir -> "Permiso pendiente"
            }
            ResumenEncabezado(textoPermiso)
            ResumenEncabezado(if (totalAvisos == 1) "1 aviso" else "$totalAvisos avisos")
        }
    }
}

@Composable
private fun ResumenEncabezado(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun TarjetaPermiso(
    permiso: PermisoUi,
    mensaje: String?,
    alPedir: () -> Unit,
    alAbrirAjustes: () -> Unit
) {
    TarjetaSeccion(
        titulo = "Permiso de notificaciones",
        subtitulo = "Necesario en Android 13 o superior",
        icono = R.drawable.ic_escudo
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when (permiso) {
                PermisoUi.SinPedir -> Etiqueta("Sin pedir", GrisAzulado, GrisSuave, conPunto = true)
                PermisoUi.Concedido -> Etiqueta("Permitido", VerdeExito, VerdeSuave, conPunto = true)
                PermisoUi.Denegado -> Etiqueta("Denegado", CoralError, CoralSuave, conPunto = true)
            }
            OutlinedButton(onClick = alPedir) {
                Text("Permitir notificaciones")
            }
        }

        // El mensaje de error aparece y se va con una animación corta
        AnimatedVisibility(
            visible = mensaje != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CoralSuave)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        painterResource(R.drawable.ic_error),
                        contentDescription = null,
                        tint = CoralError,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        mensaje.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Button(onClick = alAbrirAjustes) {
                    Text("Abrir ajustes")
                }
            }
        }
    }
}

@Composable
private fun TarjetaRecordatorio(
    faltan: Int?,
    corriendo: Boolean,
    alRecordar: () -> Unit,
    alCancelar: () -> Unit
) {
    // El anillo se vacía suavemente con cada segundo
    val progreso by animateFloatAsState(
        targetValue = (faltan ?: SEGUNDOS_RECORDATORIO) / SEGUNDOS_RECORDATORIO.toFloat(),
        animationSpec = tween(durationMillis = 900),
        label = "progresoRecordatorio"
    )

    TarjetaSeccion(
        titulo = "Recordatorio",
        subtitulo = "Programa un aviso urgente en 10 segundos",
        icono = R.drawable.ic_alarma,
        colorIcono = AmbarRecordatorio,
        fondoIcono = AmbarSuave
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(84.dp)) {
                CircularProgressIndicator(
                    progress = { if (corriendo) progreso else 1f },
                    modifier = Modifier.size(84.dp),
                    color = if (corriendo) AmbarRecordatorio else CelesteSuave,
                    trackColor = AmbarSuave,
                    strokeWidth = 7.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${faltan ?: SEGUNDOS_RECORDATORIO}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (corriendo) MaterialTheme.colorScheme.onSurface else GrisAzulado
                    )
                    Text(
                        "seg",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = faltan?.let { "Faltan $it s" } ?: "Sin recordatorio activo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Button(onClick = alRecordar, enabled = !corriendo) {
                    Text("Recordar en 10 s")
                }
                OutlinedButton(onClick = alCancelar, enabled = corriendo) {
                    Text("Cancelar")
                }
            }
        }
    }
}
