package com.abad.pokedex.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abad.pokedex.data.repository.PokemonRepository
import com.abad.pokedex.domain.model.Pokemon
import kotlinx.coroutines.flow.*

data class PerfilUiState(
    val cargando: Boolean = true,
    val totalPokemones: Int = 0,
    val totalFavoritos: Int = 0,
    val favoritos: List<Pokemon> = emptyList(),
    val error: String? = null
)

class PerfilViewModel(repository: PokemonRepository) : ViewModel() {
    val estado = combine(repository.pokemones, repository.favoritos) { catalogo, favoritos ->
        PerfilUiState(
            cargando = false,
            totalPokemones = catalogo.size,
            // Incluye las marcas conservadas aunque se haya limpiado el catálogo local.
            totalFavoritos = repository.cantidadFavoritos(),
            favoritos = favoritos.take(3)
        )
    }.catch {
        emit(PerfilUiState(cargando = false, error = "No se pudo consultar tu colección."))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerfilUiState())
}

class PerfilViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PerfilViewModel::class.java))
        return PerfilViewModel(repository) as T
    }
}
