package com.abad.pokedex

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abad.pokedex.domain.model.Pokemon
import com.abad.pokedex.domain.model.Rol
import com.abad.pokedex.ui.admin.AdminScreen
import com.abad.pokedex.ui.admin.OperacionAdmin
import com.abad.pokedex.ui.perfil.PerfilScreen
import com.abad.pokedex.ui.perfil.PerfilUiState
import com.abad.pokedex.ui.theme.PokedexAbadTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PerfilAdminUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun perfilMuestraDatosYConfirmaSalida() {
        var favoritos = 0
        var salidas = 0
        compose.setContent {
            PokedexAbadTheme {
                Scaffold { padding ->
                    Box(Modifier.padding(padding)) {
                        PerfilScreen("prueba@example.com", "Cuenta de prueba", {}, { salidas++ }, Rol.ADMIN,
                            PerfilUiState(false, 60, 3, listOf(
                                Pokemon("bulbasaur", null, listOf("grass")),
                                Pokemon("charmander", null, listOf("fire")),
                                Pokemon("squirtle", null, listOf("water"))
                            )), { favoritos++ })
                    }
                }
            }
        }
        compose.onAllNodesWithText("Cuenta de prueba").onFirst().assertIsDisplayed()
        compose.onNodeWithText("60").assertExists()
        captura("perfil-superior.png")
        compose.onNodeWithText("Ver mis favoritos").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, favoritos) }
        captura("perfil-coleccion.png")
        compose.onNodeWithText("Cerrar sesión").performScrollTo().performClick()
        compose.onNodeWithText("¿Cerrar sesión?").assertIsDisplayed()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertEquals(0, salidas) }
        compose.onNodeWithText("Cerrar sesión").performClick()
        compose.onAllNodesWithText("Cerrar sesión").onLast().performClick()
        compose.runOnIdle { assertEquals(1, salidas) }
    }

    @Test fun adminSincronizaYExigeConfirmarLimpieza() {
        var sincronizaciones = 0
        var limpiezas = 0
        compose.setContent {
            PokedexAbadTheme {
                Scaffold { padding ->
                    Box(Modifier.padding(padding)) {
                        AdminScreen(60, 3, { limpiezas++ }, { sincronizaciones++ }, nombre = "Cuenta de prueba",
                            distribucionTipos = mapOf("grass" to 24, "water" to 20, "fire" to 16, "poison" to 12))
                    }
                }
            }
        }
        compose.onNodeWithText("Centro de control\nPokédex").assertIsDisplayed()
        captura("admin-superior.png")
        compose.onNodeWithText("Diversidad Pokémon").performScrollTo()
        captura("admin-tipos.png")
        compose.onNodeWithText("Sincronizar catálogo").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, sincronizaciones) }
        captura("admin-gestion.png")
        compose.onNodeWithText("Limpiar caché local").performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertEquals(0, limpiezas) }
        compose.onNodeWithText("Limpiar caché local").performClick()
        compose.onNodeWithText("Limpiar", substring = false).performClick()
        compose.runOnIdle { assertEquals(1, limpiezas) }
    }

    @Test fun adminCompactoConFuenteGrandeDeshabilitaAccionesDuranteOperacion() {
        compose.setContent {
            val densidad = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(densidad.density, 1.5f)) {
                PokedexAbadTheme(darkTheme = true) {
                    Scaffold { padding ->
                        Box(Modifier.padding(padding).width(320.dp)) {
                            AdminScreen(0, 0, {}, {}, cargando = true, nombre = "Nombre de prueba extenso",
                                operacion = OperacionAdmin.SINCRONIZAR)
                        }
                    }
                }
            }
        }
        compose.onNodeWithText("Sincronizar catálogo").performScrollTo().assertIsNotEnabled()
        captura("admin-compacto-oscuro.png")
        compose.onNodeWithText("Limpiar caché local").performScrollTo().assertIsNotEnabled()
    }

    private fun captura(nombre: String) {
        compose.waitForIdle()
        val i = InstrumentationRegistry.getInstrumentation()
        val archivo = File(i.targetContext.getExternalFilesDir(null), nombre)
        i.uiAutomation.takeScreenshot().let { bitmap ->
            archivo.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
        }
    }
}
