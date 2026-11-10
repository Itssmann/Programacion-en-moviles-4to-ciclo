package com.abad.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.abad.pokedex.ui.lista.PokemonListScreen
import com.abad.pokedex.ui.lista.PokemonViewModel
import com.abad.pokedex.ui.lista.PokemonViewModelFactory
import com.abad.pokedex.ui.theme.PokedexAbadTheme

class MainActivity : ComponentActivity() {

    private val container by lazy {
        AppContainer()
    }

    private val pokemonViewModel: PokemonViewModel by viewModels {
        PokemonViewModelFactory(container.pokemonRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PokedexAbadTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        PokemonListScreen(
                            viewModel = pokemonViewModel
                        )
                    }
                }
            }
        }
    }
}
