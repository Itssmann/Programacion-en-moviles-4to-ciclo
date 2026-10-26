package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.relation.MascotaConDueno
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class PacientesViewModel(
    repository: VetRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    private val _especieFiltro = MutableStateFlow("TODOS")
    val especieFiltro = _especieFiltro.asStateFlow()

    val pacientes: StateFlow<List<MascotaConDueno>> =
        combine(
            repository.observarTodasMascotas(),
            _busqueda,
            _especieFiltro
        ) { lista, texto, especie ->
            val query = texto.trim()
            lista.filter { item ->
                val coincideTexto = query.isBlank() ||
                        item.mascota.nombre.contains(query, ignoreCase = true) ||
                        item.dueno.nombre.contains(query, ignoreCase = true)

                val coincideEspecie = especie == "TODOS" || item.mascota.especie.equals(especie, ignoreCase = true)

                coincideTexto && coincideEspecie
            }.sortedBy { it.mascota.nombre }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun filtrarEspecie(especie: String) {
        _especieFiltro.value = especie
    }

    fun limpiarBusqueda() {
        _busqueda.value = ""
        _especieFiltro.value = "TODOS"
    }

    fun edad(anioNacimiento: Int): Int =
        (LocalDate.now().year - anioNacimiento).coerceAtLeast(0)
}
