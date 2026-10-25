package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import com.abad.huellitasvet.data.repository.VetRepository
import com.abad.huellitasvet.model.Horarios
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PedirCitaViewModel(
    private val repository: VetRepository,
    private val duenoId: Int,
    preferencias: PreferenciasStore
) : ViewModel() {

    val aceptarCitas: StateFlow<Boolean> =
        preferencias.aceptarCitas.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            true
        )

    val mascotas: StateFlow<List<Mascota>> =
        repository.observarMascotas(duenoId)
            .map { lista -> lista.map { it.mascota } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val servicios: StateFlow<List<Servicio>> =
        repository.observarServiciosActivos()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    private val _mascotaId = MutableStateFlow<Int?>(null)
    val mascotaId = _mascotaId.asStateFlow()

    private val _servicioId = MutableStateFlow<Int?>(null)
    val servicioId = _servicioId.asStateFlow()

    private val _fecha = MutableStateFlow(LocalDate.now())
    val fecha = _fecha.asStateFlow()

    private val _hora = MutableStateFlow<String?>(null)
    val hora = _hora.asStateFlow()

    private val _nota = MutableStateFlow("")
    val nota = _nota.asStateFlow()

    val horasOcupadas: StateFlow<List<String>> =
        _fecha.flatMapLatest { dia ->
            repository.observarHorasOcupadas(dia.toString())
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _confirmada = MutableStateFlow(false)
    val confirmada = _confirmada.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando = _guardando.asStateFlow()

    val horarios = Horarios.horas

    fun elegirMascota(id: Int) {
        _mascotaId.value = id
        _error.value = null
    }

    fun elegirServicio(id: Int) {
        _servicioId.value = id
        _error.value = null
    }

    fun elegirFecha(dia: LocalDate) {
        if (dia !in LocalDate.now()..LocalDate.now().plusDays(7)) {
            _error.value = "La fecha debe estar dentro de los próximos 7 días"
            return
        }
        _fecha.value = dia
        _hora.value = null
        _error.value = null
    }

    fun elegirHora(hora: String) {
        if (!horaDisponible(hora)) {
            _error.value = "Ese horario no está disponible"
            return
        }
        _hora.value = hora
        _error.value = null
    }

    fun escribirNota(texto: String) {
        _nota.value = texto
    }

    fun horaDisponible(hora: String): Boolean {
        if (hora !in horarios) return false
        if (hora in horasOcupadas.value) return false

        val fechaHora = LocalDateTime.of(
            _fecha.value,
            LocalTime.parse(hora)
        )

        return fechaHora.isAfter(LocalDateTime.now())
    }

    fun confirmar() {
        if (_guardando.value) return

        val idMascota = _mascotaId.value
        val idServicio = _servicioId.value
        val horaElegida = _hora.value

        if (idMascota == null || idServicio == null || horaElegida == null) {
            _error.value = "Selecciona mascota, servicio, fecha y hora"
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _error.value = null

            try {
                require(aceptarCitas.value) {
                    "La clínica no está aceptando nuevas citas en este momento"
                }

                val hoy = LocalDate.now()
                val dia = _fecha.value

                require(dia in hoy..hoy.plusDays(7)) {
                    "Fecha fuera del rango permitido"
                }

                require(horaDisponible(horaElegida)) {
                    "El horario ya no está disponible"
                }

                val mascota = mascotas.value.firstOrNull {
                    it.id == idMascota && it.duenoId == duenoId
                }
                require(mascota != null) {
                    "Selecciona una mascota de tu cuenta"
                }

                val servicio = repository.obtenerServicio(idServicio)
                require(servicio?.activo == true) {
                    "El servicio no está disponible"
                }

                require(
                    repository.contarOcupadas(
                        dia.toString(),
                        horaElegida
                    ) == 0
                ) {
                    "El horario acaba de ocuparse"
                }

                repository.crearCita(
                    mascotaId = idMascota,
                    servicioId = idServicio,
                    fecha = dia.toString(),
                    hora = horaElegida,
                    nota = _nota.value.trim()
                )

                _confirmada.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "No se pudo reservar la cita"
            } finally {
                _guardando.value = false
            }
        }
    }
}

