package com.abad.saludpluscitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.abad.saludpluscitas.navigation.AppNavigation
import com.abad.saludpluscitas.ui.theme.SaludPlusCitasTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SaludPlusCitasTheme {
                AppNavigation()
            }
        }
    }
}