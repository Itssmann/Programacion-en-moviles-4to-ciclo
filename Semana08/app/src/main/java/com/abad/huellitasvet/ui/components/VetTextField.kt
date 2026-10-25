package com.abad.huellitasvet.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun VetTextField(
    valor: String,
    onValorCambio: (String) -> Unit,
    etiqueta: String,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = {
            error?.let { Text(it) }
        },
        modifier = modifier.fillMaxWidth()
    )
}
