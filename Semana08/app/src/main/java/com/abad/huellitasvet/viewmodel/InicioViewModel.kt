package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.relation.CitaConDetalle
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class InicioViewModel(
    repository: VetRepository,
    duenoId: Int
) : ViewModel() {

    val mascotas: StateFlow<List<Mascota>> =
        repository.observarMascotas(duenoId)
            .map { lista -> lista.map { it.mascota } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val proximaCita: StateFlow<CitaConDetalle?> =
        repository.observarCitasDueno(duenoId)
            .map { lista ->
                val ahora = LocalDateTime.now()

                lista.filter { detalle ->
                    val cita = detalle.cita
                    val fechaHora = LocalDateTime.of(
                        LocalDate.parse(cita.fecha),
                        LocalTime.parse(cita.hora)
                    )

                    cita.estado in listOf("PENDIENTE", "CONFIRMADA") &&
                            !fechaHora.isBefore(ahora)
                }.minWithOrNull(
                    compareBy(
                        { it.cita.fecha },
                        { it.cita.hora }
                    )
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )
}

