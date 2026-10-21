package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class PanelViewModel(
    private val repository: VetRepository
) : ViewModel() {

    private val hoy = LocalDate.now().toString()
    private val inicioMes = LocalDate.now()
        .withDayOfMonth(1).toString()

    val citasHoy: StateFlow<Int> =
        repository.contarCitasFecha(hoy)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    val pendientes: StateFlow<Int> =
        repository.contarCitasEstado("PENDIENTE")
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    val atendidas: StateFlow<Int> =
        repository.contarCitasEstado("ATENDIDA")
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )

    val ingresosMes: StateFlow<Double> =
        repository.ingresosDesde(inicioMes)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )

    val porEstado: StateFlow<List<Pair<String, Int>>> =
        combine(
            repository.contarCitasEstado("PENDIENTE"),
            repository.contarCitasEstado("CONFIRMADA"),
            repository.contarCitasEstado("ATENDIDA"),
            repository.contarCitasEstado("CANCELADA")
        ) { pendiente, confirmada, atendida, cancelada ->
            listOf(
                "Pendientes" to pendiente,
                "Confirmadas" to confirmada,
                "Atendidas" to atendida,
                "Canceladas" to cancelada
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val porServicio = repository.contarPorServicio()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}
