package com.abad.pokedex

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import org.junit.Assert.assertEquals
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abad.pokedex.data.local.AppDatabase
import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.data.remote.dto.*
import com.abad.pokedex.data.repository.PokemonRepositoryImpl
import com.abad.pokedex.ui.detalle.*
import com.abad.pokedex.ui.lista.*
import com.abad.pokedex.ui.favoritos.*
import com.abad.pokedex.ui.navigation.AppNavigation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritosNavigationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun filtrosCatalogoDetalleYFavoritosMantienenNavegacion() {
        val db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
        val api = ApiDePantallas()
        val repo = PokemonRepositoryImpl(api, db.pokemonDao())
        val restauracion = StateRestorationTester(compose)
        val store = ViewModelStore()
        lateinit var lista: PokemonViewModel
        lateinit var detalle: DetalleViewModel
        lateinit var favoritos: FavoritosViewModel
        compose.runOnIdle {
            lista = ViewModelProvider(store, PokemonViewModelFactory(repo))[PokemonViewModel::class.java]
            detalle = ViewModelProvider(store, DetalleViewModelFactory(repo))[DetalleViewModel::class.java]
            favoritos = ViewModelProvider(store, FavoritosViewModelFactory(repo))[FavoritosViewModel::class.java]
        }
        try {
            restauracion.setContent {
                MaterialTheme { AppNavigation(lista, detalle, favoritos) }
            }
            esperar("Charmander")
            compose.onNodeWithText("Agua").performClick()
            esperar("Squirtle")
            compose.onNodeWithText("Planta").performClick()
            esperar("Bulbasaur")
            compose.onNodeWithText("Fuego").performClick()
            esperar("Charmander")
            compose.onNodeWithText("♡").performClick()
            esperar("♥")
            compose.onNodeWithText("♥ Mis favoritos").performClick()
            esperar("Charmander")
            compose.onNodeWithText("Charmander").performClick()
            esperar("Información general")
            compose.onNodeWithText("♥").performClick()
            esperar("♡")
            compose.onNodeWithText("← Volver al catálogo").performClick()
            esperar("Todavía no tienes Pokémon favoritos.")
            compose.onNodeWithText("← Volver al catálogo").performClick()
            esperar("Charmander")
            compose.onNodeWithText("♡").assertIsDisplayed()
            compose.onNodeWithText("Charmander").performClick()
            esperar("Información general")
            compose.onNodeWithText("♡").performClick()
            esperar("♥")
            compose.onNodeWithText("← Volver al catálogo").performClick()
            esperar("Charmander")
            compose.onNodeWithText("♥").assertIsDisplayed()
            compose.onNodeWithText("Agua").performClick()
            esperar("Squirtle")
            compose.onNodeWithText("Squirtle").performClick()
            esperar("Información general")
            compose.onNodeWithText("← Volver al catálogo").performClick()
            esperar("Squirtle")
            compose.onNodeWithText("Agua").assertIsSelected()
            compose.onNodeWithText("♥ Mis favoritos").performClick()
            esperar("Charmander")
            compose.onNodeWithText("← Volver al catálogo").performClick()
            esperar("Squirtle")
            restauracion.emulateSavedInstanceStateRestore()
            esperar("Squirtle")
            compose.onNodeWithText("Agua").assertIsSelected()
            compose.runOnIdle { assertEquals(3, api.consultasLista) }
            compose.onNodeWithText("Actualizar").performClick()
            compose.waitUntil(10_000) { !lista.refrescando.value }
            compose.runOnIdle { assertEquals(4, api.consultasLista) }
        } finally {
            compose.runOnIdle { store.clear() }
            db.close()
        }
    }

    private fun esperar(texto: String) {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(texto).assertIsDisplayed()
    }

    private class ApiDePantallas : PokemonApiService {
        var consultasLista = 0
        private val nombres = mapOf("fire" to "charmander", "water" to "squirtle", "grass" to "bulbasaur")
        override suspend fun obtenerPorTipo(tipo: String): PokemonTypeResponse {
            consultasLista++
            return PokemonTypeResponse(listOf(PokemonTypeEntry(PokemonReference(nombres.getValue(tipo), ""))))
        }
        override suspend fun obtenerDetalle(nombre: String) = PokemonDetailDto(
            1, nombre, 7, 85, PokemonSprites(null),
            listOf(PokemonTypeSlot(PokemonTypeName(nombres.entries.first { it.value == nombre }.key))),
            emptyList(), emptyList()
        )
    }
}
