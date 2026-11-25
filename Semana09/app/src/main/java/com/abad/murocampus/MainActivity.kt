package com.abad.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abad.murocampus.data.PostRepositoryRemote
import com.abad.murocampus.data.remote.RetrofitClient
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
                PantallaLista(vm)
            }
        }
    }
}
