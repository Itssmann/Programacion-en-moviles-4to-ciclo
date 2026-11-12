package com.abad.pokedex.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.abad.pokedex.R
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
import com.abad.pokedex.ui.perfil.PerfilViewModel
import com.abad.pokedex.ui.perfil.PerfilUiState

@Composable
fun AppNavigation(
    pokemonViewModel: PokemonViewModel,
    detalleViewModel: DetalleViewModel,
    favoritosViewModel: FavoritosViewModel,
    loginViewModel: LoginViewModel? = null,
    adminViewModel: AdminViewModel? = null,
    perfilViewModel: PerfilViewModel? = null
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

    val nombre = sesion?.nombre

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
                        },
                        onPokemonClick = { nombrePokemon ->
                            pokemonSeleccionado = nombrePokemon
                            pantallaAnterior = "inicio"
                            pantalla = "detalle"
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
                        rol = sesion?.rol,
                        coleccion = perfilViewModel?.estado?.collectAsStateWithLifecycle()?.value ?: PerfilUiState(),
                        onFavoritos = { pantalla = "favoritos" },
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
                                mensaje = adminEstado.mensaje,
                                nombre = nombre ?: correo,
                                distribucionTipos = adminEstado.distribucionTipos,
                                datosCargados = adminEstado.datosCargados,
                                esError = adminEstado.esError,
                                operacion = adminEstado.operacion
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
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal con ABAD en rojo
        Text(
            text = buildAnnotatedString {
                append("POKÉDEX ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("ABAD")
                }
            },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Tu aventura Pokémon comienza aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Imagen de bienvenida (Bulbasaur, Pikachu, Charmander, Squirtle)
        Image(
            painter = painterResource(id = R.drawable.pokemon_bienvenida),
            contentDescription = "Pokémon de bienvenida",
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Bienvenido, Entrenador!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Inicia sesión para explorar Pokémon, guardar tus favoritos y gestionar tu colección.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onLogin,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                "Iniciar sesión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRegistro,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                "Crear cuenta nueva",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
