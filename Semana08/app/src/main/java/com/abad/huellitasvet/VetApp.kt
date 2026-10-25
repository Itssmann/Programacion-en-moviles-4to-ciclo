package com.abad.huellitasvet

import android.app.Application
import com.abad.huellitasvet.data.local.DatosIniciales
import com.abad.huellitasvet.data.local.VetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VetApp : Application() {

    lateinit var database: VetDatabase
        private set

    private val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        database = VetDatabase.obtener(this)

        applicationScope.launch {
            DatosIniciales.sembrar(database)
        }
    }
}

