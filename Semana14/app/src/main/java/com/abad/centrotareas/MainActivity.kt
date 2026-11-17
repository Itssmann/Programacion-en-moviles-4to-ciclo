package com.abad.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abad.centrotareas.data.TareasRepositoryLocal
import com.abad.centrotareas.ui.PantallaTareas
import com.abad.centrotareas.viewmodel.TareasViewModel
import com.abad.centrotareas.viewmodel.TareasViewModelFactory
import com.abad.centrotareas.ui.theme.CentroTareasTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = TareasRepositoryLocal()

        val factory = TareasViewModelFactory(repository)

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
