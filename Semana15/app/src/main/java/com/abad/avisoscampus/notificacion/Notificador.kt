package com.abad.avisoscampus.notificacion

import android.content.Context

object Notificador {
    const val CANAL = "avisos"

    fun crearCanal(context: Context) {
        // TODO Paso 1: si SDK_INT >= O, crear NotificationChannel y registrarlo
    }

    // TODO Paso 2: fun tienePermiso(context: Context): Boolean

    // TODO Paso 3: fun mostrar(context: Context, aviso: Aviso): Boolean
}
