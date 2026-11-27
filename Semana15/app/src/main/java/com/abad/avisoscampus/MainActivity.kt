package com.abad.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abad.avisoscampus.data.AvisosRepositoryMemoria
import com.abad.avisoscampus.ui.PantallaAvisos
import com.abad.avisoscampus.ui.theme.AvisosCampusTheme
import com.abad.avisoscampus.viewmodel.AvisosViewModel
import com.abad.avisoscampus.viewmodel.AvisosViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = AvisosViewModelFactory(AvisosRepositoryMemoria())

        setContent {
            AvisosCampusTheme {
                val vm: AvisosViewModel = viewModel(factory = factory)
                PantallaAvisos(vm)
            }
        }
    }
}
