package com.abad.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.murocampus.data.PostRepository
import com.abad.murocampus.data.mensajeDe
import com.abad.murocampus.model.Comentario
import com.abad.murocampus.model.NuevoPost
import com.abad.murocampus.model.Post
import kotlinx.coroutines.Job
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

    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    private val _publicacion = MutableStateFlow<UiState<Post>?>(null)
    val publicacion: StateFlow<UiState<Post>?> = _publicacion.asStateFlow()

    private val _refrescando = MutableStateFlow(false)
    val refrescando: StateFlow<Boolean> = _refrescando.asStateFlow()

    private var cargaLista: Job? = null

    init {
        cargar()
    }

    fun cargar() = cargarLista(mostrarCarga = true)

    fun refrescar() = cargarLista(mostrarCarga = false)

    private fun cargarLista(mostrarCarga: Boolean) {
        cargaLista?.cancel()
        cargaLista = viewModelScope.launch {
            if (mostrarCarga) _lista.value = UiState.Cargando else _refrescando.value = true
            try {
                val userId = _usuarioFiltro.value
                val resultado = if (userId == null) repo.listar() else repo.porUsuario(userId)
                resultado
                    .onSuccess { _lista.value = UiState.Exito(it) }
                    .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
            } finally {
                _refrescando.value = false
            }
        }
    }

    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _publicacion.value = UiState.Cargando
            val nuevo = NuevoPost(userId = 1, title = titulo, body = texto)
            repo.crear(nuevo)
                .onSuccess { _publicacion.value = UiState.Exito(it) }
                .onFailure { _publicacion.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun reiniciarPublicacion() {
        _publicacion.value = null
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
