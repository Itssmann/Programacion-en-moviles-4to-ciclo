package com.abad.murocampus

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abad.murocampus.data.PostRepositoryRemote
import com.abad.murocampus.data.remote.RetrofitClient
import com.abad.murocampus.ui.PantallaCrear
import com.abad.murocampus.ui.PantallaDetalle
import com.abad.murocampus.ui.PantallaLista
import com.abad.murocampus.ui.theme.MuroCampusTheme
import com.abad.murocampus.viewmodel.PostsViewModel
import com.abad.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        val factory = PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
        setContent {
            MuroCampusTheme {
                val vm: PostsViewModel = viewModel(factory = factory)
                AppMuroCampus(vm)
            }
        }
    }
}

sealed interface Pantalla {
    data object Lista : Pantalla
    data class Detalle(val id: Int) : Pantalla
    data object Crear : Pantalla
}

@Composable
fun AppMuroCampus(vm: PostsViewModel) {
    var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }
    val estadoGuardado = rememberSaveableStateHolder()

    val id = idSeleccionado
    val pantalla: Pantalla = when {
        creando -> Pantalla.Crear
        id != null -> Pantalla.Detalle(id)
        else -> Pantalla.Lista
    }

    AnimatedContent(
        targetState = pantalla,
        transitionSpec = {
            val haciaAdelante = targetState != Pantalla.Lista
            val desplazamiento = if (haciaAdelante) 1 else -1
            (slideInHorizontally { it / 4 * desplazamiento } + fadeIn())
                .togetherWith(slideOutHorizontally { -it / 4 * desplazamiento } + fadeOut())
        },
        label = "navegacion"
    ) { actual ->
        when (actual) {
            Pantalla.Crear -> PantallaCrear(
                vm = vm,
                onVolver = { creando = false }
            )
            is Pantalla.Detalle -> PantallaDetalle(
                vm = vm,
                idPost = actual.id,
                onVolver = { idSeleccionado = null }
            )
            Pantalla.Lista -> estadoGuardado.SaveableStateProvider("lista") {
                PantallaLista(
                    vm = vm,
                    onAbrirPost = { nuevoId ->
                        idSeleccionado = nuevoId
                        vm.abrir(nuevoId)
                    },
                    onCrear = { creando = true }
                )
            }
        }
    }
}
