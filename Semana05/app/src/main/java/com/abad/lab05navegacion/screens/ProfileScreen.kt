package com.abad.lab05navegacion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.abad.lab05navegacion.model.UserRepository
import com.abad.lab05navegacion.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val primaryPurple = Color(0xFF6A4C93)
    val lightPurple = Color(0xFF8B62C0)

    val currentUser = UserRepository.currentUser
    val userName = currentUser?.name ?: "Invitado"
    val userEmail = currentUser?.email ?: "invitado@tecsup.edu.pe"
    val userPhone = currentUser?.phone ?: "Sin teléfono registrado"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración de Perfil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryPurple
                )
            )
        },
        containerColor = Color(0xFFF8F5FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Gradient Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryPurple,
                                lightPurple
                            )
                        )
                    )
                    .padding(vertical = 28.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                color = primaryPurple.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto de perfil",
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Perfil Estudiantil",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 1: INFORMACIÓN PERSONAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "INFORMACIÓN PERSONAL",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryPurple,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Nombre Completo",
                        value = userName
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFEEEEEE)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Correo",
                        value = userEmail
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFEEEEEE)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Teléfono",
                        value = userPhone
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 2: ACADÉMICO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "ACADÉMICO",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryPurple,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoRow(
                        icon = Icons.Default.School,
                        label = "Carrera",
                        value = "Diseño y Desarrollo de Software"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFEEEEEE)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.DateRange,
                        label = "Ciclo Actual",
                        value = "4to Ciclo"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Cerrar Sesión
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        UserRepository.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Cerrar Sesión",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    val primaryPurple = Color(0xFF6A4C93)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = primaryPurple.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = primaryPurple,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF221A2E)
                )
            )
        }
    }
}
