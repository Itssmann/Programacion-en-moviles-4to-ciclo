package com.abad.huellitasvet.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titulo: String,
    onVolver: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Text("←")
                }
            }
        }
    )
}

