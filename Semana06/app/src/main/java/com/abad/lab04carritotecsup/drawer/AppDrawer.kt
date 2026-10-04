package com.abad.lab04carritotecsup.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abad.lab04carritotecsup.navigation.Screen

@Composable
fun AppDrawer(
    rutaActual: String,
    favoritoCount: Int,
    onNavigate: (String) -> Unit
) {
    ModalDrawerSheet {
        // Encabezado del usuario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(58.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LA",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Luis Abad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "luis.abad@tecsup.edu.pe",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        HorizontalDivider()

        Spacer(modifier = Modifier.height(12.dp))

        NavigationDrawerItem(
            label = { Text("Inicio") },
            icon = {
                Icon(Icons.Default.Home, contentDescription = null)
            },
            selected = rutaActual == Screen.Home.route,
            onClick = {
                onNavigate(Screen.Home.route)
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("Mis pedidos") },
            icon = {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
            },
            selected = rutaActual == Screen.Pedidos.route,
            onClick = {
                onNavigate(Screen.Pedidos.route)
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("Favoritos") },
            icon = {
                Icon(Icons.Default.Favorite, contentDescription = null)
            },
            badge = {
                if (favoritoCount > 0) {
                    Badge {
                        Text("$favoritoCount")
                    }
                }
            },
            selected = rutaActual == Screen.Favoritos.route,
            onClick = {
                onNavigate(Screen.Favoritos.route)
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("Perfil") },
            icon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            selected = rutaActual == Screen.Perfil.route,
            onClick = {
                onNavigate(Screen.Perfil.route)
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Cerrar sesión") },
            icon = {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
            },
            selected = false,
            onClick = { },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}