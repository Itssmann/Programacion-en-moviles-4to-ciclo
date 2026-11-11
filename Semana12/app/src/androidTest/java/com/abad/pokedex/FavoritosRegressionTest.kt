package com.abad.pokedex

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abad.pokedex.data.auth.SesionActual
import com.abad.pokedex.data.local.AppDatabase
import com.abad.pokedex.data.local.PokemonEntity
import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.data.remote.dto.*
import com.abad.pokedex.data.repository.PokemonRepositoryImpl
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.detalle.DetalleViewModel
import com.abad.pokedex.ui.detalle.DetalleViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FavoritosRegressionTest {

    private val uid = "usuario-prueba"

    private lateinit var db: AppDatabase
    private lateinit var api: ApiFalsa
    private lateinit var sesion: SesionActual
    private lateinit var repo: PokemonRepositoryImpl

    @Before
    fun preparar() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()

        api = ApiFalsa()
        sesion = SesionActual.paraPruebas(uid)

        repo = PokemonRepositoryImpl(
            api,
            db.pokemonDao(),
            sesion
        )
    }

    @After
    fun cerrar() {
        sesion.cerrar()
        db.close()
    }

    private fun entidad(nombre: String = "charmander") =
        PokemonEntity(
            nombre = nombre,
            imagenUrl = null,
            tipos = "[\"fire\"]",
            habilidades = "[]",
            estadisticas = "{}"
        )

    @Test
    fun sincronizacionConcurrenteConservaMarcarYDesmarcar() =
        runBlocking {
            withTimeout(10_000) {
                val dao = db.pokemonDao()
                dao.guardarTodos(listOf(entidad()))

                for (esperado in listOf(true, false)) {
                    api.pausarEn = "charmeleon"
                    api.entrada = CompletableDeferred()
                    api.continuar = CompletableDeferred()

                    val refresco = async {
                        repo.refrescar("fire").getOrThrow()
                    }

                    api.entrada.await()

                    repo.alternarFavorito("charmander")
                    api.continuar.complete(Unit)

                    assertEquals(2, refresco.await())

                    assertEquals(
                        esperado,
                        dao.esFavoritoDeUsuario(uid, "charmander")
                    )

                    assertEquals(
                        7,
                        dao.obtenerPorNombre("charmander")!!.altura
                    )

                    assertEquals(
                        esperado,
                        repo.favoritos.first().any {
                            it.nombre == "charmander"
                        }
                    )
                }
            }
        }

    @Test
    fun actualizacionRemotaNoEscribeFavoritoEnNingunaDireccion() =
        runBlocking {
            val dao = db.pokemonDao()
            dao.guardarTodos(listOf(entidad()))

            dao.alternarFavoritoDeUsuario(uid, "charmander")

            dao.guardarTodos(
                listOf(
                    entidad().copy(
                        esFavorito = false,
                        peso = 99
                    )
                )
            )

            assertTrue(
                dao.esFavoritoDeUsuario(uid, "charmander")
            )

            dao.alternarFavoritoDeUsuario(uid, "charmander")

            dao.guardarTodos(
                listOf(
                    entidad().copy(esFavorito = true)
                )
            )

            assertFalse(
                dao.esFavoritoDeUsuario(uid, "charmander")
            )
        }

    @Test
    fun alternanciasConcurrentesSonAtomicas() =
        runBlocking {
            withTimeout(10_000) {
                val dao = db.pokemonDao()
                dao.guardarTodos(listOf(entidad()))

                coroutineScope {
                    repeat(101) {
                        launch(Dispatchers.Default) {
                            repo.alternarFavorito("charmander")
                        }
                    }
                }

                assertTrue(
                    dao.esFavoritoDeUsuario(uid, "charmander")
                )

                repo.alternarFavorito("charmander")

                assertFalse(
                    dao.esFavoritoDeUsuario(uid, "charmander")
                )
            }
        }

    @Test
    fun detalleRemotoYOfflineConservanAmbosValores() =
        runBlocking {
            db.pokemonDao().guardarTodos(listOf(entidad()))

            for (offline in listOf(false, true)) {
                api.offline = offline

                for (esperado in listOf(true, false)) {
                    repo.alternarFavorito("charmander")

                    assertEquals(
                        esperado,
                        repo.obtenerDetalle("charmander")
                            .getOrThrow()
                            .esFavorito
                    )
                }
            }
        }

    @Test
    fun detalleObservaCambiosExternosYPublicaSusCambiosEnRoom() =
        runBlocking {
            withTimeout(15_000) {
                db.pokemonDao().guardarTodos(listOf(entidad()))

                val store = ViewModelStore()

                val vm = withContext(Dispatchers.Main) {
                    ViewModelProvider(
                        store,
                        DetalleViewModelFactory(repo)
                    )[DetalleViewModel::class.java]
                }

                try {
                    for (offline in listOf(false, true)) {
                        api.offline = offline

                        withContext(Dispatchers.Main) {
                            vm.cargarDetalle("charmander")
                        }

                        vm.estado.first {
                            it is UiState.Exito &&
                                    !it.datos.esFavorito
                        }

                        for (esperado in listOf(true, false)) {
                            repo.alternarFavorito("charmander")

                            vm.estado.first {
                                it is UiState.Exito &&
                                        it.datos.esFavorito == esperado
                            }
                        }

                        for (esperado in listOf(true, false)) {
                            withContext(Dispatchers.Main) {
                                vm.alternarFavorito()
                            }

                            repo.pokemones.first {
                                it.single().esFavorito == esperado
                            }

                            vm.estado.first {
                                it is UiState.Exito &&
                                        it.datos.esFavorito == esperado
                            }

                            assertEquals(
                                esperado,
                                repo.favoritos.first().isNotEmpty()
                            )
                        }
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        store.clear()
                    }
                }
            }
        }

    @Test
    fun dosUsuariosNoCompartenFavoritos() =
        runBlocking {
            val dao = db.pokemonDao()

            dao.guardarTodos(
                listOf(
                    entidad("charmander"),
                    entidad("bulbasaur")
                )
            )

            // Cuenta A
            repo.alternarFavorito("charmander")

            assertTrue(
                dao.esFavoritoDeUsuario(uid, "charmander")
            )

            // Cuenta B
            sesion.cambiarUsuarioDePrueba("usuario-b")

            assertTrue(repo.favoritos.first().isEmpty())

            repo.alternarFavorito("bulbasaur")

            assertTrue(
                dao.esFavoritoDeUsuario("usuario-b", "bulbasaur")
            )

            assertFalse(
                dao.esFavoritoDeUsuario("usuario-b", "charmander")
            )

            // Volver a la cuenta A
            sesion.cambiarUsuarioDePrueba(uid)

            val favoritosA = repo.favoritos.first()

            assertEquals(
                listOf("charmander"),
                favoritosA.map { it.nombre }
            )

            assertEquals(1, repo.cantidadFavoritos())

            // Comprobar nuevamente la cuenta B
            sesion.cambiarUsuarioDePrueba("usuario-b")

            val favoritosB = repo.favoritos.first()

            assertEquals(
                listOf("bulbasaur"),
                favoritosB.map { it.nombre }
            )
        }

    private class ApiFalsa : PokemonApiService {

        var offline = false
        var pausarEn: String? = null
        var entrada = CompletableDeferred<Unit>()
        var continuar = CompletableDeferred<Unit>()

        override suspend fun obtenerListado(limite: Int, offset: Int): PokemonListResponse =
            error("Este escenario no usa el listado Admin")
        override suspend fun obtenerPorTipo(
            tipo: String
        ): PokemonTypeResponse {
            if (offline) {
                throw IOException("Sin conexión de prueba")
            }

            return PokemonTypeResponse(
                listOf("charmander", "charmeleon").map {
                    PokemonTypeEntry(
                        PokemonReference(it, "")
                    )
                }
            )
        }

        override suspend fun obtenerDetalle(
            nombre: String
        ): PokemonDetailDto {
            if (offline) {
                throw IOException("Sin conexión de prueba")
            }

            if (nombre == pausarEn) {
                entrada.complete(Unit)
                continuar.await()
            }

            return PokemonDetailDto(
                4,
                nombre,
                7,
                85,
                PokemonSprites(null),
                listOf(
                    PokemonTypeSlot(
                        PokemonTypeName("fire")
                    )
                ),
                emptyList(),
                emptyList()
            )
        }
    }
}
