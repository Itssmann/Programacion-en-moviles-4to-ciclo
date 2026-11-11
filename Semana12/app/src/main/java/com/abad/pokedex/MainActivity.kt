package com.abad.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.abad.pokedex.data.auth.FirebaseAuthRepository
import com.abad.pokedex.ui.login.LoginViewModel
import com.abad.pokedex.ui.login.LoginViewModelFactory
import com.abad.pokedex.ui.detalle.DetalleViewModel
import com.abad.pokedex.ui.detalle.DetalleViewModelFactory
import com.abad.pokedex.ui.favoritos.FavoritosViewModel
import com.abad.pokedex.ui.favoritos.FavoritosViewModelFactory
import com.abad.pokedex.ui.lista.PokemonViewModel
import com.abad.pokedex.ui.lista.PokemonViewModelFactory
import com.abad.pokedex.ui.navigation.AppNavigation
import com.abad.pokedex.ui.theme.PokedexAbadTheme

class MainActivity : ComponentActivity() {

    private val container by lazy {
        AppContainer(applicationContext)
    }

    private val pokemonViewModel: PokemonViewModel by viewModels {
        PokemonViewModelFactory(container.pokemonRepository)
    }

    private val detalleViewModel: DetalleViewModel by viewModels {
        DetalleViewModelFactory(container.pokemonRepository)
    }

    private val favoritosViewModel: FavoritosViewModel by viewModels {
        FavoritosViewModelFactory(container.pokemonRepository)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(FirebaseAuthRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PokedexAbadTheme {
                AppNavigation(
                    pokemonViewModel = pokemonViewModel,
                    detalleViewModel = detalleViewModel,
                    favoritosViewModel = favoritosViewModel,
                    loginViewModel = loginViewModel
                )
            }
        }
    }
}
