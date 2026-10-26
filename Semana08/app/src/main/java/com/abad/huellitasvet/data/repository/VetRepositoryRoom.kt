package com.abad.huellitasvet.data.repository

import androidx.room.withTransaction
import com.abad.huellitasvet.data.local.VetDatabase
import com.abad.huellitasvet.data.local.entity.*
import com.abad.huellitasvet.data.local.relation.*
import com.abad.huellitasvet.model.ConteoServicio
import kotlinx.coroutines.flow.Flow

class VetRepositoryRoom(
    private val db: VetDatabase
) : VetRepository {

    private val usuarios = db.usuarioDao()
    private val mascotas = db.mascotaDao()
    private val servicios = db.servicioDao()
    private val citas = db.citaDao()

    override suspend fun login(correo: String, clave: String): Usuario? {
        val usuario = usuarios.obtenerPorCorreo(correo.trim())
        if (usuario != null && com.abad.huellitasvet.data.local.PasswordUtils.verifyPassword(clave, usuario.clave)) {
            return usuario
        }
        return null
    }

    override suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario? =
        usuarios.obtenerPorCorreo(correo.trim())

    override suspend fun registrarUsuario(nombre: String, correo: String, clave: String, rol: String): Long {
        val correoTrim = correo.trim().lowercase()
        val existente = usuarios.obtenerPorCorreo(correoTrim)
        require(existente == null) { "El correo ya está registrado" }

        val hashedPassword = com.abad.huellitasvet.data.local.PasswordUtils.hashPassword(clave)
        return usuarios.insertar(
            Usuario(
                nombre = nombre.trim(),
                correo = correoTrim,
                clave = hashedPassword,
                rol = rol
            )
        )
    }

    override fun observarMascotas(duenoId: Int): Flow<List<MascotaConDueno>> =
        mascotas.observarPorDueno(duenoId)

    override fun observarMascota(id: Int): Flow<MascotaConDueno?> =
        mascotas.observarUna(id)

    override fun observarTodasMascotas(): Flow<List<MascotaConDueno>> =
        mascotas.observarTodas()

    override suspend fun crearMascota(mascota: Mascota): Long =
        mascotas.insertar(mascota)

    override suspend fun actualizarMascota(mascota: Mascota) =
        mascotas.actualizar(mascota)

    override suspend fun eliminarMascota(mascota: Mascota) =
        mascotas.eliminar(mascota)

    override suspend fun mascotaTieneCitas(id: Int): Boolean =
        mascotas.contarCitas(id) > 0

    override fun observarServicios(): Flow<List<Servicio>> =
        servicios.observarTodos()

    override fun observarServiciosActivos(): Flow<List<Servicio>> =
        servicios.observarActivos()

    override suspend fun obtenerServicio(id: Int): Servicio? =
        servicios.obtener(id)

    override suspend fun guardarServicio(servicio: Servicio) {
        if (servicio.id == 0) {
            servicios.insertar(servicio)
        } else {
            servicios.actualizar(servicio)
        }
    }

    override suspend fun eliminarServicio(servicio: Servicio) {
        if (servicioTieneCitas(servicio.id)) {
            servicios.actualizar(servicio.copy(activo = false))
        } else {
            servicios.eliminar(servicio)
        }
    }

    override suspend fun servicioTieneCitas(id: Int): Boolean =
        servicios.contarCitas(id) > 0

    override suspend fun crearCita(
        mascotaId: Int,
        servicioId: Int,
        fecha: String,
        hora: String,
        nota: String
    ): Long = db.withTransaction {
        val servicio = servicios.obtener(servicioId)
            ?: throw IllegalArgumentException("Servicio no encontrado")

        require(servicio.activo) { "El servicio no está activo" }

        require(citas.contarOcupadas(fecha, hora) == 0) {
            "El horario ya está ocupado"
        }

        citas.insertar(
            Cita(
                mascotaId = mascotaId,
                servicioId = servicioId,
                fecha = fecha,
                hora = hora,
                nota = nota,
                estado = "PENDIENTE",
                precio = servicio.precio,
                fechaCreacion = System.currentTimeMillis()
            )
        )
    }

    override fun observarCitasDueno(duenoId: Int): Flow<List<CitaConDetalle>> =
        citas.observarPorDueno(duenoId)

    override fun observarCitasFecha(fecha: String): Flow<List<CitaConDetalle>> =
        citas.observarPorFecha(fecha)

    override fun observarCita(id: Int): Flow<CitaConDetalle?> =
        citas.observarUna(id)

    override fun observarHistorial(mascotaId: Int): Flow<List<CitaConDetalle>> =
        citas.observarHistorial(mascotaId)

    override fun observarHorasOcupadas(fecha: String): Flow<List<String>> =
        citas.observarHorasOcupadas(fecha)

    override suspend fun contarOcupadas(fecha: String, hora: String): Int =
        citas.contarOcupadas(fecha, hora)

    override suspend fun cambiarEstadoCita(id: Int, estado: String) =
        citas.actualizarEstado(id, estado)

    override suspend fun registrarAtencion(
        id: Int,
        diagnostico: String,
        tratamiento: String,
        peso: Double
    ) {
        db.withTransaction {
            val cita = citas.obtener(id)
                ?: throw IllegalArgumentException("Cita no encontrada")

            require(diagnostico.trim().length >= 5) {
                "El diagnóstico debe tener al menos 5 caracteres"
            }
            require(peso > 0) { "El peso debe ser mayor que cero" }
            require(cita.estado == "PENDIENTE" || cita.estado == "CONFIRMADA") {
                "Esta cita no se puede atender"
            }

            citas.registrarAtencion(
                id = id,
                diagnostico = diagnostico,
                tratamiento = tratamiento,
                peso = peso
            )
            mascotas.actualizarPeso(cita.mascotaId, peso)
        }
    }

    override fun contarCitasFecha(fecha: String): Flow<Int> =
        citas.contarPorFecha(fecha)

    override fun contarCitasEstado(estado: String): Flow<Int> =
        citas.contarPorEstado(estado)

    override fun contarPorServicio(): Flow<List<ConteoServicio>> =
        citas.contarPorServicio()

    override fun ingresosDesde(fechaInicio: String): Flow<Double> =
        citas.ingresosDesde(fechaInicio)

    override fun observarPesosHistorial(mascotaId: Int): Flow<List<CitaConDetalle>> =
        citas.observarPesosHistorial(mascotaId)

    override suspend fun reprogramarCita(id: Int, fecha: String, hora: String) =
        citas.reprogramarCita(id, fecha, hora)

    override suspend fun contarOcupadasExcluyendo(fecha: String, hora: String, citaId: Int): Int =
        citas.contarOcupadasExcluyendo(fecha, hora, citaId)
}

