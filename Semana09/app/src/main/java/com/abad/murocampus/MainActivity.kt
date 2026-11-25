package com.abad.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
        enableEdgeToEdge()
        val factory = PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
        setContent {
            MuroCampusTheme {
                val vm: PostsViewModel = viewModel(factory = factory)
                AppMuroCampus(vm)
            }
        }
    }
}

@Composable
fun AppMuroCampus(vm: PostsViewModel) {
    var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }

    val id = idSeleccionado
    when {
        creando -> PantallaCrear(
            vm = vm,
            onVolver = { creando = false }
        )
        id != null -> PantallaDetalle(
            vm = vm,
            idPost = id,
            onVolver = { idSeleccionado = null }
        )
        else -> PantallaLista(
            vm = vm,
            onAbrirPost = { nuevoId ->
                idSeleccionado = nuevoId
                vm.abrir(nuevoId)
            },
            onCrear = { creando = true }
        )
    }
}
