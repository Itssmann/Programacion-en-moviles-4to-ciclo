package com.abad.pokedex

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.abad.pokedex.data.local.AppDatabase
import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.data.remote.dto.*
import com.abad.pokedex.data.repository.PokemonRepositoryImpl
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.lista.PokemonViewModel
import com.abad.pokedex.ui.lista.PokemonViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import com.abad.pokedex.data.auth.SesionActual

@RunWith(AndroidJUnit4::class)
class PokemonOfflineTest {
    private val fuego = Pokemon("charmander", null, listOf("fire"))
    private val agua = Pokemon("squirtle", null, listOf("water"))

    @Test fun cargaInicialNoPublicaVacioMientrasDescarga() = runBlocking {
        probar { repo, vm, _ ->
            val peticion = repo.peticiones.receive()
            withContext(Dispatchers.Main) {
                assertEquals(UiState.Cargando, vm.estado.value)
                vm.cargar("fire")
                vm.refrescar()
            }
            assertTrue(repo.peticiones.tryReceive().isFailure)
            repo.datos.value = listOf(fuego)
            peticion.complete(Result.success(1))
            vm.refrescando.first { !it }
            assertEquals(listOf(fuego), (vm.estado.value as UiState.Exito).datos)
        }
    }

    @Test fun descargaExitosaSinDatosPublicaVacio() = runBlocking {
        probar { repo, vm, _ ->
            repo.peticiones.receive().complete(Result.success(0))
            vm.estado.first { it is UiState.Exito }
            assertTrue((vm.estado.value as UiState.Exito).datos.isEmpty())
            assertNull(vm.aviso.value)
        }
    }

    @Test fun sinCacheErrorYReintentoVuelvenACargaYRecuperanDatos() = runBlocking {
        probar { repo, vm, _ ->
            repo.peticiones.receive().complete(Result.failure(IOException()))
            vm.estado.first { it is UiState.Error }
            assertTrue((vm.estado.value as UiState.Error).mensaje.contains("conexión"))
            withContext(Dispatchers.Main) { vm.refrescar() }
            val reintento = repo.peticiones.receive()
            assertEquals(UiState.Cargando, vm.estado.value)
            repo.datos.value = listOf(fuego)
            reintento.complete(Result.success(1))
            vm.refrescando.first { !it }
            assertEquals(listOf(fuego), (vm.estado.value as UiState.Exito).datos)
        }
    }

    @Test fun conCacheConservaListaYAvisoHastaRecuperarConexion() = runBlocking {
        probar(listOf(fuego)) { repo, vm, _ ->
            val peticion = repo.peticiones.receive()
            vm.estado.first { it is UiState.Exito }
            peticion.complete(Result.failure(IOException()))
            vm.aviso.first { it != null }
            assertEquals("Sin conexión. Mostrando datos guardados", vm.aviso.value)
            assertEquals(listOf(fuego), (vm.estado.value as UiState.Exito).datos)
            withContext(Dispatchers.Main) { vm.refrescar() }
            val reintento = repo.peticiones.receive()
            assertNotNull(vm.aviso.value)
            assertTrue(vm.estado.value is UiState.Exito)
            reintento.complete(Result.success(1))
            vm.refrescando.first { !it }
            assertNull(vm.aviso.value)
        }
    }

    @Test fun cacheDeOtroTipoNoOcultaErrorDelTipoSeleccionado() = runBlocking {
        probar(listOf(agua)) { repo, vm, _ ->
            repo.peticiones.receive().complete(Result.failure(IOException()))
            vm.estado.first { it is UiState.Error }
            assertNull(vm.aviso.value)
        }
    }

    @Test fun seleccionRetenidaNoRepitePeticionesAlRecuperarViewModel() = runBlocking {
        probar(listOf(fuego, agua)) { repo, vm, store ->
            repo.peticiones.receive().complete(Result.success(2))
            vm.refrescando.first { !it }
            withContext(Dispatchers.Main) { vm.cargar("water") }
            repo.peticiones.receive().complete(Result.failure(IOException()))
            vm.refrescando.first { !it }
            withContext(Dispatchers.Main) {
                val retenido = ViewModelProvider(store, PokemonViewModelFactory(repo))[PokemonViewModel::class.java]
                assertSame(vm, retenido)
                assertEquals("water", retenido.tipoActual.value)
                assertEquals(listOf(agua), (retenido.estado.value as UiState.Exito).datos)
                assertNotNull(retenido.aviso.value)
                retenido.cargar("fire")
                retenido.cargar("water")
            }
            assertTrue(repo.peticiones.tryReceive().isFailure)
            assertEquals(listOf(agua), (vm.estado.value as UiState.Exito).datos)
        }
    }

    @Test fun respuestaDeOtroTipoNoReemplazaEstadoActual() = runBlocking {
        probar { repo, vm, _ ->
            val peticionFuego = repo.peticiones.receive()
            withContext(Dispatchers.Main) { vm.cargar("water") }
            val peticionAgua = repo.peticiones.receive()
            repo.datos.value = listOf(agua)
            peticionAgua.complete(Result.success(1))
            vm.refrescando.first { !it }
            peticionFuego.complete(Result.failure(IOException()))
            withContext(Dispatchers.Main) { yield() }
            assertEquals("water", vm.tipoActual.value)
            assertEquals(listOf(agua), (vm.estado.value as UiState.Exito).datos)
            assertNull(vm.aviso.value)
        }
    }

    @Test fun roomRealRecuperaConexionYConservaListaDetalleYFavoritosOffline() = runBlocking {
        withTimeout(10_000) {
            val db = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext, AppDatabase::class.java
            ).build()
            var offline = true
            val api = object : PokemonApiService {
                override suspend fun obtenerPorTipo(tipo: String): PokemonTypeResponse {
                    if (offline) throw IOException("Sin conexión de prueba")
                    return PokemonTypeResponse(listOf(PokemonTypeEntry(PokemonReference("charmander", ""))))
                }
                override suspend fun obtenerDetalle(nombre: String): PokemonDetailDto {
                    if (offline) throw IOException("Sin conexión de prueba")
                    return PokemonDetailDto(4, nombre, 7, 85, PokemonSprites(null),
                        listOf(PokemonTypeSlot(PokemonTypeName("fire"))), emptyList(), emptyList())
                }
            }
            val repo = PokemonRepositoryImpl(
                api,
                db.pokemonDao(),
                SesionActual.paraPruebas()
            )
            val store = ViewModelStore()
            val vm = withContext(Dispatchers.Main) {
                ViewModelProvider(store, PokemonViewModelFactory(repo))[PokemonViewModel::class.java]
            }
            try {
                vm.estado.first { it is UiState.Error }
                withContext(Dispatchers.Main) { offline = false; vm.refrescar() }
                vm.refrescando.first { !it }
                assertEquals(1, db.pokemonDao().contarTodos())
                assertEquals("charmander", (vm.estado.value as UiState.Exito).datos.single().nombre)
                withContext(Dispatchers.Main) { offline = true; vm.refrescar() }
                vm.aviso.first { it != null }
                assertTrue(vm.estado.value is UiState.Exito)
                withContext(Dispatchers.Main) { vm.alternarFavorito("charmander") }
                vm.estado.first { it is UiState.Exito && it.datos.single().esFavorito }
                assertTrue(repo.obtenerDetalle("charmander").getOrThrow().esFavorito)
                assertEquals(1, repo.favoritos.first().size)
            } finally {
                withContext(Dispatchers.Main) { store.clear() }
                db.close()
            }
        }
    }

    private suspend fun probar(
        inicial: List<Pokemon> = emptyList(),
        bloque: suspend (RepoFalso, PokemonViewModel, ViewModelStore) -> Unit
    ) = withTimeout(10_000) {
        val repo = RepoFalso(inicial)
        val store = ViewModelStore()
        val vm = withContext(Dispatchers.Main) {
            ViewModelProvider(store, PokemonViewModelFactory(repo))[PokemonViewModel::class.java]
        }
        try { bloque(repo, vm, store) }
        finally { withContext(Dispatchers.Main) { store.clear() } }
    }

    private class RepoFalso(inicial: List<Pokemon>) : PokemonRepository {
        val datos = MutableStateFlow(inicial)
        val peticiones = Channel<CompletableDeferred<Result<Int>>>(Channel.UNLIMITED)
        override val pokemones = datos.asStateFlow()
        override val favoritos = datos.map { lista -> lista.filter { it.esFavorito } }
        override fun observarDetalle(nombre: String) = datos.map { lista -> lista.find { it.nombre == nombre } }
        override suspend fun refrescar(tipo: String): Result<Int> {
            val respuesta = CompletableDeferred<Result<Int>>()
            peticiones.send(respuesta)
            return respuesta.await()
        }
        override suspend fun obtenerDetalle(nombre: String) = Result.success(datos.value.first { it.nombre == nombre })
        override suspend fun alternarFavorito(nombre: String) {
            datos.value = datos.value.map { if (it.nombre == nombre) it.copy(esFavorito = !it.esFavorito) else it }
        }
        override suspend fun cantidadGuardada() = datos.value.size
        override suspend fun cantidadFavoritos() = datos.value.count { it.esFavorito }
        override suspend fun limpiarCache() { datos.value = emptyList() }
    }
}
