package com.abad.pokedex

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abad.pokedex.data.local.AppDatabase
import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.data.remote.dto.*
import com.abad.pokedex.data.repository.PokemonRepositoryImpl
import com.abad.pokedex.ui.detalle.*
import com.abad.pokedex.ui.favoritos.*
import com.abad.pokedex.ui.lista.*
import com.abad.pokedex.ui.navigation.AppNavigation
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritosNavigationTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun filtrosCatalogoDetalleYFavoritosMantienenNavegacion() {
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
            lista = ViewModelProvider(
                store,
                PokemonViewModelFactory(repo)
            )[PokemonViewModel::class.java]

            detalle = ViewModelProvider(
                store,
                DetalleViewModelFactory(repo)
            )[DetalleViewModel::class.java]

            favoritos = ViewModelProvider(
                store,
                FavoritosViewModelFactory(repo)
            )[FavoritosViewModel::class.java]
        }

        try {
            restauracion.setContent {
                MaterialTheme {
                    AppNavigation(lista, detalle, favoritos)
                }
            }

            // 1. Pantalla inicial
            esperar("¡Bienvenido, entrenador!")

            compose.onNodeWithText(
                "Pokémon",
                useUnmergedTree = true
            ).performClick()

            // 2. Filtros del catálogo
            esperar("Charmander")

            seleccionarFiltro("Agua")
            esperar("Squirtle")

            seleccionarFiltro("Planta")
            esperar("Bulbasaur")

            seleccionarFiltro("Fuego")
            esperar("Charmander")

            // 3. Abrir detalle desde catálogo
            compose.onNodeWithText("Charmander").performClick()
            esperar("Información general")

            // 4. Agregar favorito
            compose.onNodeWithContentDescription(
                "Agregar a favoritos"
            ).performClick()

            compose.onNodeWithContentDescription(
                "Quitar de favoritos"
            ).assertExists()

            // 5. Regresar al catálogo
            compose.onNodeWithText("Volver").performClick()
            esperar("Charmander")
            comprobarFiltroSeleccionado("Fuego")

            // 6. Abrir Favoritos desde la barra inferior
            compose.onNodeWithText(
                "Favoritos",
                useUnmergedTree = true
            ).performClick()

            esperar("Mis favoritos")
            esperar("Charmander")

            // 7. Abrir detalle desde Favoritos
            compose.onNodeWithText("Charmander").performClick()
            esperar("Información general")

            // 8. Quitar favorito
            compose.onNodeWithContentDescription(
                "Quitar de favoritos"
            ).performClick()

            compose.onNodeWithContentDescription(
                "Agregar a favoritos"
            ).assertExists()

            // 9. Regresar a Favoritos
            compose.onNodeWithText("Volver").performClick()
            esperar("Aún no tienes favoritos")

            // 10. Volver al catálogo
            compose.onNodeWithText(
                "Pokémon",
                useUnmergedTree = true
            ).performClick()

            esperar("Charmander")
            comprobarFiltroSeleccionado("Fuego")

            // 11. Cambiar a Agua
            seleccionarFiltro("Agua")
            esperar("Squirtle")
            comprobarFiltroSeleccionado("Agua")

            // 12. Abrir y cerrar detalle
            compose.onNodeWithText("Squirtle").performClick()
            esperar("Información general")

            compose.onNodeWithText("Volver").performClick()
            esperar("Squirtle")
            comprobarFiltroSeleccionado("Agua")

            // 13. Verificar el ViewModel antes de restaurar
            compose.runOnIdle {
                assertEquals("water", lista.tipoActual.value)
            }

            // 14. Restaurar estado de Compose
            restauracion.emulateSavedInstanceStateRestore()

            esperar("Squirtle")
            comprobarFiltroSeleccionado("Agua")

            compose.runOnIdle {
                assertEquals("water", lista.tipoActual.value)
                assertEquals(3, api.consultasLista)
            }

            // 15. Actualización manual
            compose.onNodeWithText("Actualizar").performClick()

            compose.waitUntil(10_000) {
                !lista.refrescando.value &&
                        api.consultasLista == 4
            }

            compose.runOnIdle {
                assertEquals(4, api.consultasLista)
            }

        } finally {
            compose.runOnIdle {
                store.clear()
            }
            db.close()
        }
    }

    private fun seleccionarFiltro(nombre: String) {
        compose.onNode(
            hasText(nombre) and isSelectable()
        ).performClick()
    }

    private fun comprobarFiltroSeleccionado(nombre: String) {
        compose.onNode(
            hasText(nombre) and isSelectable()
        ).assertIsSelected()
    }

    private fun esperar(texto: String) {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText(texto)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        compose.onNodeWithText(texto).assertIsDisplayed()
    }

    private class ApiDePantallas : PokemonApiService {
        var consultasLista = 0

        private val nombres = mapOf(
            "fire" to "charmander",
            "water" to "squirtle",
            "grass" to "bulbasaur"
        )

        override suspend fun obtenerPorTipo(
            tipo: String
        ): PokemonTypeResponse {
            consultasLista++

            return PokemonTypeResponse(
                listOf(
                    PokemonTypeEntry(
                        PokemonReference(
                            nombres.getValue(tipo),
                            ""
                        )
                    )
                )
            )
        }

        override suspend fun obtenerDetalle(
            nombre: String
        ) = PokemonDetailDto(
            1,
            nombre,
            7,
            85,
            PokemonSprites(null),
            listOf(
                PokemonTypeSlot(
                    PokemonTypeName(
                        nombres.entries.first {
                            it.value == nombre
                        }.key
                    )
                )
            ),
            emptyList(),
            emptyList()
        )
    }
}
