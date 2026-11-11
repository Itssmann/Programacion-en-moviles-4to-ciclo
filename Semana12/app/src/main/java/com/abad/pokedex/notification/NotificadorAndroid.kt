package com.abad.pokedex.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.abad.pokedex.MainActivity
import com.abad.pokedex.R

class NotificadorAndroid(context: Context) : Notificador {
    private val contexto = context.applicationContext
    private val manager = NotificationManagerCompat.from(contexto)
    private var solicitandoPermiso = false
    private var pendiente: Pair<String, String>? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel(
                CANAL, "Sincronización de Pokémon", NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Resultados de las sincronizaciones del catálogo" })
        }
    }

    @Synchronized
    fun prepararSolicitudPermiso() { solicitandoPermiso = true }

    @Synchronized
    fun resolverPermiso(concedido: Boolean) {
        solicitandoPermiso = false
        val aviso = pendiente
        pendiente = null
        if (concedido && aviso != null) mostrar(aviso.first, aviso.second)
    }

    @Synchronized
    override fun mostrar(titulo: String, texto: String) {
        if (solicitandoPermiso) {
            // La primera descarga puede terminar mientras el diálogo sigue abierto.
            pendiente = titulo to texto
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) return
        if (!manager.areNotificationsEnabled()) return

        val abrirApp = PendingIntent.getActivity(
            contexto, 0,
            Intent(contexto, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificacion = NotificationCompat.Builder(contexto, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()
        try {
            manager.notify(ID_SINCRONIZACION, notificacion)
        } catch (_: SecurityException) {
            // El permiso puede revocarse entre la comprobación y notify().
        }
    }

    companion object {
        const val CANAL = "sincronizacion_pokemon"
        const val ID_SINCRONIZACION = 15
        @Volatile private var compartido: NotificadorAndroid? = null

        fun instancia(context: Context): NotificadorAndroid = compartido ?: synchronized(this) {
            compartido ?: NotificadorAndroid(context).also { compartido = it }
        }
    }
}
