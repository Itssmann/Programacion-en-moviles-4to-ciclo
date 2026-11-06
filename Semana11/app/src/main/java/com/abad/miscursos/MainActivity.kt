
package com.abad.miscursos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.abad.miscursos.ui.theme.MisCursosAbadTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MisCursosAbadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AuthApp()
                }
            }
        }
    }
}
