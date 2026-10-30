package com.abad.huellitasvet.data.repository

import com.abad.huellitasvet.data.local.entity.*
import com.abad.huellitasvet.data.local.relation.*
import com.abad.huellitasvet.model.ConteoServicio
import kotlinx.coroutines.flow.Flow

interface VetRepository {

    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario?
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?
    suspend fun registrarUsuario(nombre: String, correo: String, clave: String, rol: String, telefono: String?, distrito: String?): Long
    suspend fun registrarUsuario(nombre: String, correo: String, clave: String, rol: String): Long

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
        servicioIds: List<Int>,
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
    suspend fun reprogramarCita(id: Int, fecha: String, hora: String)
    suspend fun contarOcupadasExcluyendo(fecha: String, hora: String, citaId: Int): Int

    fun observarNotificaciones(usuarioId: Int): Flow<List<Notificacion>>
    fun contarNotificacionesNoLeidas(usuarioId: Int): Flow<Int>
    suspend fun marcarNotificacionLeida(id: Int)
    suspend fun marcarTodasNotificacionesLeidas(usuarioId: Int)
    suspend fun eliminarNotificacion(id: Int)

    fun observarServiciosDeCita(citaId: Int): Flow<List<Servicio>>
    fun observarUsuarios(): Flow<List<Usuario>>
    fun observarTodasCitas(): Flow<List<CitaConDetalle>>
}
