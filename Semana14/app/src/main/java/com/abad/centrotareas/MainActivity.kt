package com.abad.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abad.centrotareas.data.TareasRepositoryLocal
import com.abad.centrotareas.data.UserPreferencesRepository
import com.abad.centrotareas.ui.PantallaTareas
import com.abad.centrotareas.ui.theme.CentroTareasTheme
import com.abad.centrotareas.viewmodel.TareasViewModel
import com.abad.centrotareas.viewmodel.TareasViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = TareasRepositoryLocal()
        val preferencesRepository = UserPreferencesRepository(applicationContext)

        val factory = TareasViewModelFactory(repository, preferencesRepository)

        setContent {
            CentroTareasTheme {
                val tareasViewModel: TareasViewModel = viewModel(
                    factory = factory
                )

                PantallaTareas(tareasViewModel)
            }
        }
    }
}
