package com.abad.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.murocampus.data.PostRepository
import com.abad.murocampus.data.mensajeDe
import com.abad.murocampus.model.Comentario
import com.abad.murocampus.model.Post
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)

class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            repo.listar()
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun abrir(id: Int) {
        viewModelScope.launch {
            _detalle.value = UiState.Cargando

            val pedidoPost = async { repo.detalle(id) }
            val pedidoComentarios = async { repo.comentarios(id) }

            val resultadoPost = pedidoPost.await()
            val resultadoComentarios = pedidoComentarios.await()

            val post = resultadoPost.getOrNull()
            val comentarios = resultadoComentarios.getOrNull()

            _detalle.value = if (post != null && comentarios != null) {
                UiState.Exito(DetalleUi(post, comentarios))
            } else {
                val error = resultadoPost.exceptionOrNull()
                    ?: resultadoComentarios.exceptionOrNull()
                UiState.Error(mensajeDe(error ?: Exception()))
            }
        }
    }
}
