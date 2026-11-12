package com.abad.pokedex

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.abad.pokedex.data.auth.FirebaseAuthRepository
import com.abad.pokedex.ui.admin.AdminViewModel
import com.abad.pokedex.ui.admin.AdminViewModelFactory
import com.abad.pokedex.ui.detalle.DetalleViewModel
import com.abad.pokedex.ui.detalle.DetalleViewModelFactory
import com.abad.pokedex.ui.favoritos.FavoritosViewModel
import com.abad.pokedex.ui.favoritos.FavoritosViewModelFactory
import com.abad.pokedex.ui.lista.PokemonViewModel
import com.abad.pokedex.ui.lista.PokemonViewModelFactory
import com.abad.pokedex.ui.login.LoginViewModel
import com.abad.pokedex.ui.login.LoginViewModelFactory
import com.abad.pokedex.ui.perfil.PerfilViewModel
import com.abad.pokedex.ui.perfil.PerfilViewModelFactory
import com.abad.pokedex.ui.navigation.AppNavigation
import com.abad.pokedex.ui.theme.PokedexAbadTheme

class MainActivity : ComponentActivity() {

    private val container by lazy {
        AppContainer(applicationContext)
    }

    // Una misma instancia para el login y el panel Admin.
    private val authRepository by lazy {
        FirebaseAuthRepository()
    }

    private val pokemonViewModel: PokemonViewModel by viewModels {
        PokemonViewModelFactory(container.pokemonRepository, container.notificador)
    }

    private val detalleViewModel: DetalleViewModel by viewModels {
        DetalleViewModelFactory(container.pokemonRepository)
    }

    private val favoritosViewModel: FavoritosViewModel by viewModels {
        FavoritosViewModelFactory(container.pokemonRepository)
    }

    private val perfilViewModel: PerfilViewModel by viewModels {
        PerfilViewModelFactory(container.pokemonRepository)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(authRepository)
    }

    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(
            repository = container.pokemonRepository,
            authRepository = authRepository,
            notificador = container.notificador
        )
    }

    private val permisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> container.notificador.resolverPermiso(concedido) }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED) return
        val preferencias = getPreferences(MODE_PRIVATE)
        if (preferencias.getBoolean("notificaciones_solicitadas", false)) return
        preferencias.edit().putBoolean("notificaciones_solicitadas", true).apply()
        container.notificador.prepararSolicitudPermiso()
        permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        solicitarPermisoNotificaciones()

        setContent {
            PokedexAbadTheme {
                AppNavigation(
                    pokemonViewModel = pokemonViewModel,
                    detalleViewModel = detalleViewModel,
                    favoritosViewModel = favoritosViewModel,
                    loginViewModel = loginViewModel,
                    adminViewModel = adminViewModel,
                    perfilViewModel = perfilViewModel
                )
            }
        }
    }
}
