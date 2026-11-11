package com.abad.pokedex.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abad.pokedex.domain.model.Rol
import com.abad.pokedex.ui.admin.AdminScreen
import com.abad.pokedex.ui.admin.AdminViewModel
import com.abad.pokedex.ui.detalle.DetalleScreen
import com.abad.pokedex.ui.detalle.DetalleViewModel
import com.abad.pokedex.ui.favoritos.FavoritosScreen
import com.abad.pokedex.ui.favoritos.FavoritosViewModel
import com.abad.pokedex.ui.inicio.InicioScreen
import com.abad.pokedex.ui.lista.PokemonListScreen
import com.abad.pokedex.ui.lista.PokemonViewModel
import com.abad.pokedex.ui.login.LoginScreen
import com.abad.pokedex.ui.login.LoginViewModel
import com.abad.pokedex.ui.login.RegistroScreen
import com.abad.pokedex.ui.perfil.PerfilScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    pokemonViewModel: PokemonViewModel,
    detalleViewModel: DetalleViewModel,
    favoritosViewModel: FavoritosViewModel,
    loginViewModel: LoginViewModel? = null,
    adminViewModel: AdminViewModel? = null
) {
    var pantalla by rememberSaveable {
        mutableStateOf("inicio")
    }

    var pantallaAnterior by rememberSaveable {
        mutableStateOf("catalogo")
    }

    var pokemonSeleccionado by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val loginEstado = loginViewModel?.estado
        ?.collectAsStateWithLifecycle()

    val sesion = loginEstado?.value?.sesion
    val correo = sesion?.correo

    val requiereAutenticacion = loginViewModel != null
    val autenticado = !requiereAutenticacion || sesion != null

    val esAdmin = sesion?.rol == Rol.ADMIN &&
            adminViewModel != null

    val nombre = if (correo != null) {
        FirebaseAuth.getInstance()
            .currentUser?.displayName
    } else {
        null
    }

    LaunchedEffect(autenticado, sesion?.uid) {
        if (!autenticado) {
            pantalla = "bienvenida"
            pokemonSeleccionado = null
        } else if (
            pantalla in listOf(
                "bienvenida",
                "login",
                "registro"
            )
        ) {
            pantalla = "inicio"
        }
    }

    // Impedir que una sesión sin rol ADMIN
    // permanezca en la pantalla administrativa.
    LaunchedEffect(esAdmin, pantalla) {
        if (pantalla == "admin" && !esAdmin) {
            pantalla = "inicio"
        }
    }

    BackHandler(
        enabled = pantalla in listOf(
            "detalle",
            "login",
            "registro",
            "admin"
        )
    ) {
        when (pantalla) {
            "detalle" -> {
                pantalla = pantallaAnterior
                pokemonSeleccionado = null
            }

            "registro" -> {
                pantalla = "login"
            }

            "login" -> {
                pantalla = "bienvenida"
            }

            "admin" -> {
                pantalla = "inicio"
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (
                autenticado &&
                pantalla !in listOf(
                    "detalle",
                    "login",
                    "registro",
                    "bienvenida"
                )
            ) {
                NavigationBar {
                    val opciones = buildList {
                        add(
                            Triple(
                                "inicio",
                                "Inicio",
                                Icons.Default.Home
                            )
                        )

                        add(
                            Triple(
                                "catalogo",
                                "Pokémon",
                                Icons.AutoMirrored.Filled.List
                            )
                        )

                        add(
                            Triple(
                                "favoritos",
                                "Favoritos",
                                Icons.Default.Favorite
                            )
                        )

                        add(
                            Triple(
                                "perfil",
                                "Perfil",
                                Icons.Default.Person
                            )
                        )

                        if (esAdmin) {
                            add(
                                Triple(
                                    "admin",
                                    "Admin",
                                    Icons.Default.AdminPanelSettings
                                )
                            )
                        }
                    }

                    opciones.forEach { (ruta, titulo, icono) ->
                        NavigationBarItem(
                            selected = pantalla == ruta,
                            onClick = {
                                pantalla = ruta

                                if (ruta == "admin") {
                                    adminViewModel?.actualizarTotales()
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = icono,
                                    contentDescription = titulo
                                )
                            },
                            label = {
                                Text(titulo)
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (
                !autenticado &&
                pantalla !in listOf(
                    "bienvenida",
                    "login",
                    "registro"
                )
            ) {
                PantallaBienvenida(
                    onLogin = {
                        pantalla = "login"
                    },
                    onRegistro = {
                        pantalla = "registro"
                    }
                )
            } else {
                when (pantalla) {

                    "bienvenida" -> {
                        PantallaBienvenida(
                            onLogin = {
                                loginViewModel?.limpiarError()
                                pantalla = "login"
                            },
                            onRegistro = {
                                loginViewModel?.limpiarError()
                                pantalla = "registro"
                            }
                        )
                    }

                    "inicio" -> InicioScreen(
                        pokemonViewModel = pokemonViewModel,
                        favoritosViewModel = favoritosViewModel,
                        onVerPokemones = {
                            pantalla = "catalogo"
                        },
                        onVerFavoritos = {
                            pantalla = "favoritos"
                        }
                    )

                    "catalogo" -> PokemonListScreen(
                        viewModel = pokemonViewModel,
                        onPokemonClick = { nombrePokemon ->
                            pokemonSeleccionado = nombrePokemon
                            pantallaAnterior = "catalogo"
                            pantalla = "detalle"
                        },
                        onFavoritosClick = {
                            pantalla = "favoritos"
                        }
                    )

                    "favoritos" -> FavoritosScreen(
                        viewModel = favoritosViewModel,
                        onPokemonClick = { nombrePokemon ->
                            pokemonSeleccionado = nombrePokemon
                            pantallaAnterior = "favoritos"
                            pantalla = "detalle"
                        },
                        onVolver = {
                            pantalla = "catalogo"
                        }
                    )

                    "perfil" -> PerfilScreen(
                        correo = correo,
                        nombre = nombre,
                        onLogin = {
                            pantalla = "login"
                        },
                        onCerrarSesion = {
                            loginViewModel?.cerrarSesion()
                        }
                    )

                    "admin" -> {
                        if (esAdmin && adminViewModel != null) {
                            val adminEstado by adminViewModel.estado
                                .collectAsStateWithLifecycle()

                            AdminScreen(
                                totalPokemones = adminEstado.totalPokemones,
                                totalFavoritos = adminEstado.totalFavoritos,
                                onLimpiarCache = {
                                    adminViewModel.limpiarCache()
                                },
                                onSincronizar = {
                                    adminViewModel.sincronizar()
                                },
                                cargando = adminEstado.cargando,
                                mensaje = adminEstado.mensaje
                            )

                        }
                    }

                    "login" -> {
                        if (loginViewModel != null) {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onRegistro = {
                                    loginViewModel.limpiarError()
                                    pantalla = "registro"
                                },
                                onVolver = {
                                    loginViewModel.limpiarError()
                                    pantalla = "bienvenida"
                                }
                            )
                        }
                    }

                    "registro" -> {
                        if (loginViewModel != null) {
                            RegistroScreen(
                                viewModel = loginViewModel,
                                onVolver = {
                                    loginViewModel.limpiarError()
                                    pantalla = "login"
                                }
                            )
                        }
                    }

                    "detalle" -> {
                        val nombrePokemon = pokemonSeleccionado

                        if (nombrePokemon != null) {
                            DetalleScreen(
                                nombre = nombrePokemon,
                                viewModel = detalleViewModel,
                                onVolver = {
                                    pantalla = pantallaAnterior
                                    pokemonSeleccionado = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PantallaBienvenida(
    onLogin: () -> Unit,
    onRegistro: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "POKÉDEX ABAD",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Bienvenido, entrenador!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Inicia sesión para explorar Pokémon y crear tu propia colección.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRegistro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear cuenta")
        }
    }
}
