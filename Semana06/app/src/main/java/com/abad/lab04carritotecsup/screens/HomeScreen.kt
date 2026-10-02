package com.abad.lab04carritotecsup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abad.lab04carritotecsup.Producto
import com.abad.lab04carritotecsup.components.TarjetaProducto

@Composable
fun HomeScreen() {

    val productos = listOf(
        Producto("Laptop Lenovo", 2499.90, 1),
        Producto("Mouse Logitech", 89.90, 1),
        Producto("Teclado Mecánico", 159.90, 1),
        Producto("Audífonos Gamer", 129.90, 1)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "TECSUP Store",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Productos destacados",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
                TarjetaProducto(
                    producto = producto,
                    onEliminar = {}
                )
            }
        }
    }
}