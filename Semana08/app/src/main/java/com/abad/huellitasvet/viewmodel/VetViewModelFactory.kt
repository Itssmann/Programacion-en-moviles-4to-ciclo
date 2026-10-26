package com.abad.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abad.huellitasvet.data.prefs.PreferenciasStore
import com.abad.huellitasvet.data.repository.VetRepository

class VetViewModelFactory(
    private val repository: VetRepository,
    private val preferencias: PreferenciasStore,
    private val duenoId: Int = 0
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SesionViewModel::class.java) ->
                SesionViewModel(repository, preferencias) as T

            modelClass.isAssignableFrom(RegistroViewModel::class.java) ->
                RegistroViewModel(repository) as T

            modelClass.isAssignableFrom(InicioViewModel::class.java) ->
                InicioViewModel(repository, duenoId) as T

            modelClass.isAssignableFrom(MascotaViewModel::class.java) ->
                MascotaViewModel(repository, duenoId) as T

            modelClass.isAssignableFrom(PedirCitaViewModel::class.java) ->
                PedirCitaViewModel(repository, duenoId, preferencias) as T

            modelClass.isAssignableFrom(MisCitasViewModel::class.java) ->
                MisCitasViewModel(repository, duenoId) as T

            modelClass.isAssignableFrom(PanelViewModel::class.java) ->
                PanelViewModel(repository) as T

            modelClass.isAssignableFrom(AgendaViewModel::class.java) ->
                AgendaViewModel(repository) as T

            modelClass.isAssignableFrom(PacientesViewModel::class.java) ->
                PacientesViewModel(repository) as T

            modelClass.isAssignableFrom(ServiciosViewModel::class.java) ->
                ServiciosViewModel(repository) as T

            else -> throw IllegalArgumentException(
                "ViewModel desconocido: ${modelClass.name}"
            )
        }
    }
}
