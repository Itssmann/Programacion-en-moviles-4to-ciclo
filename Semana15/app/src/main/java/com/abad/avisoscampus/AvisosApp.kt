package com.abad.avisoscampus

import android.app.Application
import com.abad.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // El canal se crea una sola vez, al iniciar la app
        Notificador.crearCanal(this)
    }
}
