package com.abad.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abad.campusgps.data.LugaresRepositoryLocal
import com.abad.campusgps.data.UbicacionRepositoryGoogle
import com.abad.campusgps.ui.PantallaMapa
import com.abad.campusgps.ui.theme.CampusGPSTheme
import com.abad.campusgps.viewmodel.CampusViewModelFactory
import com.abad.campusgps.viewmodel.MapaViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ubicacionRepo = UbicacionRepositoryGoogle(applicationContext)
        val lugaresRepo = LugaresRepositoryLocal()

        val factory = CampusViewModelFactory(
            ubicacionRepo = ubicacionRepo,
            lugaresRepo = lugaresRepo
        )

        setContent {
            CampusGPSTheme {
                val mapaViewModel: MapaViewModel = viewModel(
                    factory = factory
                )

                PantallaMapa(viewModel = mapaViewModel)
            }
        }
    }
}
