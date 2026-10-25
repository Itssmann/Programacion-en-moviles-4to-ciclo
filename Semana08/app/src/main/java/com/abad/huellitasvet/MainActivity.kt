
package com.abad.huellitasvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import com.abad.huellitasvet.data.repository.VetRepositoryRoom
import com.abad.huellitasvet.ui.navigation.NavVet

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as VetApp
        val repository = VetRepositoryRoom(app.database)
        val preferencias = PreferenciasStore(applicationContext)

        setContent {
            NavVet(
                repository = repository,
                preferencias = preferencias
            )
        }
    }
}
