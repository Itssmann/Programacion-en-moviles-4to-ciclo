package com.abad.lab04carritotecsup.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    rutaActual: String,
    onNavigate: (String) -> Unit
) {
    ModalDrawerSheet {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "TECSUP Store",
                fontWeight = FontWeight.Bold
            )
            Text("Menú principal")
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            label = { Text("Inicio") },
            selected = rutaActual == "home",
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            onClick = { onNavigate("home") }
        )

        NavigationDrawerItem(
            label = { Text("Mis pedidos") },
            selected = rutaActual == "pedidos",
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            onClick = { onNavigate("pedidos") }
        )

        NavigationDrawerItem(
            label = { Text("Favoritos") },
            selected = rutaActual == "favoritos",
            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
            onClick = { onNavigate("favoritos") }
        )

        NavigationDrawerItem(
            label = { Text("Perfil") },
            selected = rutaActual == "perfil",
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            onClick = { onNavigate("perfil") }
        )
    }
}