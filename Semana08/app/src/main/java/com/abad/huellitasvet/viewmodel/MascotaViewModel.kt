package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.relation.CitaConDetalle
import com.abad.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MascotaViewModel(
    private val repository: VetRepository,
    private val duenoId: Int
) : ViewModel() {

    private val _errores = MutableStateFlow<Map<String, String>>(emptyMap())
    val errores: StateFlow<Map<String, String>> = _errores.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    fun mascota(id: Int) = repository.observarMascota(id)

    fun historial(mascotaId: Int): StateFlow<List<CitaConDetalle>> =
        repository.observarHistorial(mascotaId)
            .map { lista ->
                lista.filter { it.cita.estado == "ATENDIDA" }
                    .sortedWith(
                        compareByDescending<CitaConDetalle> { it.cita.fecha }
                            .thenByDescending { it.cita.hora }
                    )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun pesosHistorial(mascotaId: Int): StateFlow<List<CitaConDetalle>> =
        repository.observarPesosHistorial(mascotaId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun limpiarEstado() {
        _errores.value = emptyMap()
        _mensaje.value = null
        _guardado.value = false
    }

    fun guardar(mascota: Mascota) {
        val erroresNuevos = mutableMapOf<String, String>()

        if (mascota.nombre.trim().length < 2) {
            erroresNuevos["nombre"] = "Mínimo 2 caracteres"
        }
        if (mascota.especie !in listOf("PERRO", "GATO", "OTRO")) {
            erroresNuevos["especie"] = "Selecciona una especie"
        }
        if (mascota.sexo !in listOf("MACHO", "HEMBRA")) {
            erroresNuevos["sexo"] = "Selecciona el sexo"
        }
        if (mascota.anioNacimiento !in 1990..LocalDate.now().year) {
            erroresNuevos["anio"] = "Año entre 1990 y ${LocalDate.now().year}"
        }
        if (!mascota.peso.isFinite() || mascota.peso <= 0) {
            erroresNuevos["peso"] = "El peso debe ser mayor a cero"
        }

        _errores.value = erroresNuevos
        if (erroresNuevos.isNotEmpty()) return

        viewModelScope.launch {
            try {
                require(mascota.duenoId == duenoId) {
                    "La mascota no pertenece al dueño actual"
                }

                if (mascota.id == 0) {
                    repository.crearMascota(mascota)
                } else {
                    val original = repository.observarMascota(mascota.id).first()
                    require(original?.mascota?.duenoId == duenoId) {
                        "No puedes editar esta mascota"
                    }
                    repository.actualizarMascota(mascota)
                }

                _guardado.value = true
                _mensaje.value = "Mascota guardada correctamente"
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo guardar"
            }
        }
    }

    fun eliminar(mascota: Mascota, onEliminada: () -> Unit) {
        viewModelScope.launch {
            try {
                require(mascota.duenoId == duenoId) {
                    "No puedes eliminar esta mascota"
                }

                if (repository.mascotaTieneCitas(mascota.id)) {
                    _mensaje.value =
                        "No se puede eliminar: la mascota tiene citas registradas"
                    return@launch
                }

                repository.eliminarMascota(mascota)
                onEliminada()
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo eliminar"
            }
        }
    }
}

