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

    val pacientes: StateFlow<List<MascotaConDueno>> =
        combine(
            repository.observarTodasMascotas(),
            _busqueda
        ) { lista, texto ->
            lista.filter {
                it.mascota.nombre.contains(texto, ignoreCase = true) ||
                        it.dueno.nombre.contains(texto, ignoreCase = true) ||
                        it.mascota.especie.contains(texto, ignoreCase = true)
            }.sortedBy { it.mascota.nombre }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun edad(anioNacimiento: Int): Int =
        (LocalDate.now().year - anioNacimiento).coerceAtLeast(0)
}

