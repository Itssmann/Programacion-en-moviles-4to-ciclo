package com.abad.huellitasvet.data.repository

import com.abad.huellitasvet.data.local.entity.*
import com.abad.huellitasvet.data.local.relation.*
import com.abad.huellitasvet.model.ConteoServicio
import kotlinx.coroutines.flow.Flow

interface VetRepository {

    suspend fun login(correo: String, clave: String): Usuario?

    fun observarMascotas(duenoId: Int): Flow<List<MascotaConDueno>>
    fun observarMascota(id: Int): Flow<MascotaConDueno?>
    fun observarTodasMascotas(): Flow<List<MascotaConDueno>>
    suspend fun crearMascota(mascota: Mascota): Long
    suspend fun actualizarMascota(mascota: Mascota)
    suspend fun eliminarMascota(mascota: Mascota)
    suspend fun mascotaTieneCitas(id: Int): Boolean

    fun observarServicios(): Flow<List<Servicio>>
    fun observarServiciosActivos(): Flow<List<Servicio>>
    suspend fun obtenerServicio(id: Int): Servicio?
    suspend fun guardarServicio(servicio: Servicio)
    suspend fun eliminarServicio(servicio: Servicio)
    suspend fun servicioTieneCitas(id: Int): Boolean

    suspend fun crearCita(
        mascotaId: Int,
        servicioId: Int,
        fecha: String,
        hora: String,
        nota: String
    ): Long

    fun observarCitasDueno(duenoId: Int): Flow<List<CitaConDetalle>>
    fun observarCitasFecha(fecha: String): Flow<List<CitaConDetalle>>
    fun observarCita(id: Int): Flow<CitaConDetalle?>
    fun observarHistorial(mascotaId: Int): Flow<List<CitaConDetalle>>
    fun observarHorasOcupadas(fecha: String): Flow<List<String>>
    suspend fun contarOcupadas(fecha: String, hora: String): Int
    suspend fun cambiarEstadoCita(id: Int, estado: String)
    suspend fun registrarAtencion(
        id: Int,
        diagnostico: String,
        tratamiento: String,
        peso: Double
    )

    fun contarCitasFecha(fecha: String): Flow<Int>
    fun contarCitasEstado(estado: String): Flow<Int>
    fun contarPorServicio(): Flow<List<ConteoServicio>>
    fun ingresosDesde(fechaInicio: String): Flow<Double>
    fun observarPesosHistorial(mascotaId: Int): Flow<List<CitaConDetalle>>
}

