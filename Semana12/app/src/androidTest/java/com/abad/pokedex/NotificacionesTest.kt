package com.abad.pokedex

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelFileDescriptor
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abad.pokedex.data.auth.AuthRepository
import com.abad.pokedex.data.auth.SesionActual
import com.abad.pokedex.data.local.AppDatabase
import com.abad.pokedex.data.remote.PokemonApiService
import com.abad.pokedex.data.remote.dto.*
import com.abad.pokedex.data.repository.PokemonRepositoryImpl
import com.abad.pokedex.domain.model.Rol
import com.abad.pokedex.domain.model.Sesion
import com.abad.pokedex.notification.Notificador
import com.abad.pokedex.notification.NotificadorAndroid
import com.abad.pokedex.ui.UiState
import com.abad.pokedex.ui.admin.AdminViewModel
import com.abad.pokedex.ui.admin.AdminViewModelFactory
import com.abad.pokedex.ui.lista.PokemonViewModel
import com.abad.pokedex.ui.lista.PokemonViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NotificacionesTest {
    private val instrumentacion = InstrumentationRegistry.getInstrumentation()
    private val contexto = instrumentacion.targetContext
    private val manager get() = contexto.getSystemService(NotificationManager::class.java)
    private fun permiso() = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
        contexto, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    private suspend fun escenario(bloque: suspend (PokemonRepositoryImpl, ApiPrueba, ViewModelStore) -> Unit) {
        val db = Room.inMemoryDatabaseBuilder(contexto, AppDatabase::class.java).build()
        val sesion = SesionActual.paraPruebas()
        val api = ApiPrueba()
        val repo = PokemonRepositoryImpl(api, db.pokemonDao(), sesion)
        val store = ViewModelStore()
        try { withTimeout(15_000) { bloque(repo, api, store) } }
        finally { withContext(Dispatchers.Main) { store.clear() }; sesion.cerrar(); db.close() }
    }

    @Test fun catalogoNotificaSoloTrasGuardarYNoAlFallar() = runBlocking {
        escenario { repo, api, store ->
            api.espera = CompletableDeferred()
            val avisos = Channel<String>(Channel.UNLIMITED)
            val notificador = Notificador { _, texto ->
                assertEquals(2, runBlocking(Dispatchers.IO) { repo.cantidadGuardada() })
                avisos.trySend(texto)
            }
            val vm = withContext(Dispatchers.Main) {
                ViewModelProvider(store, PokemonViewModelFactory(repo, notificador))[PokemonViewModel::class.java]
            }
            api.iniciada.await()
            assertEquals(UiState.Cargando, vm.estado.value)
            assertTrue(avisos.tryReceive().isFailure)
            api.espera!!.complete(Unit)
            assertEquals("Se guardaron 2 elementos", avisos.receive())
            vm.estado.first { it is UiState.Exito && it.datos.size == 2 }
            withContext(Dispatchers.Main) { api.fallar = true; vm.refrescar() }
            vm.refrescando.first { !it }
            assertTrue(avisos.tryReceive().isFailure)
            assertTrue(vm.estado.value is UiState.Exito)
        }
    }

    @Test fun adminNotificaCantidadDescargadaNoTotalCacheYRespetaRol() = runBlocking {
        escenario { repo, api, store ->
            repo.refrescar("fire").getOrThrow() // dos filas previas
            val avisos = Channel<String>(Channel.UNLIMITED)
            val auth = AuthPrueba()
            val vm = withContext(Dispatchers.Main) {
                ViewModelProvider(store, AdminViewModelFactory(repo, auth,
                    Notificador { _, texto -> avisos.trySend(texto) }))[AdminViewModel::class.java]
            }
            withContext(Dispatchers.Main) { vm.sincronizar() }
            assertEquals("Se guardaron 3 elementos", avisos.receive())
            vm.estado.first { !it.cargando && it.totalPokemones == 5 }
            withContext(Dispatchers.Main) { api.fallar = true; vm.sincronizar() }
            vm.estado.first { !it.cargando }
            assertTrue(avisos.tryReceive().isFailure)
            withContext(Dispatchers.Main) { auth.rol = Rol.USUARIO; vm.sincronizar() }
            vm.estado.first { !it.cargando }
            assertTrue(avisos.tryReceive().isFailure)
        }
    }

    @Test fun permisoDenegadoNoPublicaYCatalogoSigueFuncionando() = runBlocking {
        assumeTrue(!permiso())
        escenario { repo, _, store ->
            val notificador = NotificadorAndroid(contexto)
            notificador.prepararSolicitudPermiso()
            notificador.mostrar("Datos actualizados", "Se guardaron 99 elementos")
            notificador.resolverPermiso(false)
            val vm = withContext(Dispatchers.Main) {
                ViewModelProvider(store, PokemonViewModelFactory(repo, notificador))[PokemonViewModel::class.java]
            }
            vm.refrescando.first { !it }
            assertEquals(2, repo.cantidadGuardada())
            assertTrue(vm.estado.value is UiState.Exito)
            assertTrue(manager.activeNotifications.none { it.id == NotificadorAndroid.ID_SINCRONIZACION })
        }
    }

    @Test fun permisoConcedidoPublicaCanalTextoYAbreMainActivityAlTocar() {
        assumeTrue(permiso())
        val notificador = NotificadorAndroid(contexto)
        try {
            notificador.prepararSolicitudPermiso()
            notificador.mostrar("Prueba H15", "Se guardaron 7 elementos")
            notificador.resolverPermiso(true)
            val aviso = esperarAviso()
            assertEquals("Se guardaron 7 elementos", aviso.extras.getString(Notification.EXTRA_TEXT))
            assertEquals(NotificadorAndroid.CANAL, aviso.channelId)
            assertNotNull(manager.getNotificationChannel(NotificadorAndroid.CANAL))
            assertTrue(aviso.contentIntent.isActivity)
            shell("input keyevent KEYCODE_WAKEUP")
            shell("wm dismiss-keyguard")
            shell("cmd statusbar expand-notifications")
            val limite = System.currentTimeMillis() + 10_000
            var pulsado = false
            while (!pulsado && System.currentTimeMillis() < limite) {
                val nodo = buscarTitulo(instrumentacion.uiAutomation.rootInActiveWindow)
                if (nodo != null && nodo.isVisibleToUser) {
                    val limites = android.graphics.Rect()
                    nodo.getBoundsInScreen(limites)
                    if (!limites.isEmpty) {
                        shell("input tap ${limites.centerX()} ${limites.centerY()}")
                        pulsado = true
                    }
                }
                if (!pulsado) Thread.sleep(100)
            }
            assertTrue("No se pudo tocar la notificación visible", pulsado)
            val fin = System.currentTimeMillis() + 10_000
            var abierta = false
            while (!abierta && System.currentTimeMillis() < fin) {
                abierta = shell("dumpsys activity activities").lineSequence().any {
                    (it.contains("topResumedActivity") || it.contains("mResumedActivity")) &&
                        it.contains("com.abad.pokedex/.MainActivity")
                }
                if (!abierta) Thread.sleep(100)
            }
            assertTrue("La notificación no abrió MainActivity", abierta)
        } finally {
            manager.cancel(NotificadorAndroid.ID_SINCRONIZACION)
            shell("cmd statusbar collapse")
        }
    }

    private fun buscarTitulo(nodo: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (nodo == null) return null
        if (nodo.text?.toString() == "Prueba H15") return nodo
        for (i in 0 until nodo.childCount) {
            buscarTitulo(nodo.getChild(i))?.let { return it }
        }
        return null
    }

    private fun esperarAviso(): Notification {
        repeat(100) {
            manager.activeNotifications.firstOrNull { it.id == NotificadorAndroid.ID_SINCRONIZACION }
                ?.let { return it.notification }
            Thread.sleep(50)
        }
        error("No apareció la notificación")
    }

    private fun shell(comando: String): String = ParcelFileDescriptor.AutoCloseInputStream(
        instrumentacion.uiAutomation.executeShellCommand(comando)
    ).bufferedReader().use { it.readText() }

    private class AuthPrueba : AuthRepository {
        var rol = Rol.ADMIN
        override val usuarioActual get() = Sesion("prueba", "prueba@example.com", rol)
        override suspend fun iniciarSesion(correo: String, contrasena: String) = Result.success(usuarioActual)
        override suspend fun registrar(nombre: String, correo: String, contrasena: String) = Result.success(usuarioActual)
        override fun cerrarSesion() {}
    }

    private class ApiPrueba : PokemonApiService {
        var fallar = false
        var espera: CompletableDeferred<Unit>? = null
        val iniciada = CompletableDeferred<Unit>()
        override suspend fun obtenerPorTipo(tipo: String): PokemonTypeResponse {
            iniciada.complete(Unit)
            espera?.await()
            if (fallar) throw IOException("Sin conexión de prueba")
            return PokemonTypeResponse(listOf("charmander", "charmeleon").map {
                PokemonTypeEntry(PokemonReference(it, ""))
            })
        }
        override suspend fun obtenerListado(limite: Int, offset: Int): PokemonListResponse {
            if (fallar) throw IOException("Sin conexión de prueba")
            return PokemonListResponse(listOf("bulbasaur", "ivysaur", "venusaur").map { PokemonListItemDto(it, "") })
        }
        override suspend fun obtenerDetalle(nombre: String) = PokemonDetailDto(
            1, nombre, 7, 85, PokemonSprites(null), listOf(PokemonTypeSlot(PokemonTypeName("fire"))),
            emptyList(), emptyList()
        )
    }
}
