package com.abad.huellitasvet.data.repository

import androidx.room.withTransaction
import com.abad.huellitasvet.data.local.VetDatabase
import com.abad.huellitasvet.data.local.entity.*
import com.abad.huellitasvet.data.local.relation.*
import com.abad.huellitasvet.model.ConteoServicio
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class VetRepositoryRoom(
    private val db: VetDatabase
) : VetRepository {

    private val usuarios = db.usuarioDao()
    private val mascotas = db.mascotaDao()
    private val servicios = db.servicioDao()
    private val citas = db.citaDao()
    private val notificaciones = db.notificacionDao()
    private val citaServicios = db.citaServicioDao()

    override suspend fun login(correo: String, clave: String): Usuario? {
        val usuario = usuarios.obtenerPorCorreo(correo.trim())
        if (usuario != null && com.abad.huellitasvet.data.local.PasswordUtils.verifyPassword(clave, usuario.clave)) {
            return usuario
        }
        return null
    }

    override suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario? =
        usuarios.obtenerPorCorreo(correo.trim())

    override suspend fun obtenerUsuarioPorId(id: Int): Usuario? =
        usuarios.obtenerPorId(id)

    override suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        clave: String,
        rol: String,
        telefono: String?,
        distrito: String?
    ): Long {
        val correoTrim = correo.trim().lowercase()
        val existente = usuarios.obtenerPorCorreo(correoTrim)
        require(existente == null) { "El correo ya está registrado" }

        val hashedPassword = com.abad.huellitasvet.data.local.PasswordUtils.hashPassword(clave)
        return usuarios.insertar(
            Usuario(
                nombre = nombre.trim(),
                correo = correoTrim,
                clave = hashedPassword,
                rol = rol,
                telefono = telefono?.trim(),
                distrito = distrito?.trim()
            )
        )
    }

    override suspend fun registrarUsuario(nombre: String, correo: String, clave: String, rol: String): Long =
        registrarUsuario(nombre, correo, clave, rol, null, null)

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
        servicioIds: List<Int>,
        fecha: String,
        hora: String,
        nota: String
    ): Long = db.withTransaction {
        require(servicioIds.isNotEmpty()) { "Debe seleccionar al menos un servicio" }

        val serviciosList = servicioIds.map { id ->
            servicios.obtener(id) ?: throw IllegalArgumentException("Servicio $id no encontrado")
        }

        require(serviciosList.all { it.activo }) { "Uno o más servicios no están activos" }

        require(citas.contarOcupadas(fecha, hora) == 0) {
            "El horario ya está ocupado"
        }

        val mascota = mascotas.obtener(mascotaId)
            ?: throw IllegalArgumentException("Mascota no encontrada")

        val precioTotal = serviciosList.sumOf { it.precio }
        val primaryServicioId = servicioIds.first()

        val citaId = citas.insertar(
            Cita(
                mascotaId = mascotaId,
                servicioId = primaryServicioId,
                fecha = fecha,
                hora = hora,
                nota = nota,
                estado = "PENDIENTE",
                precio = precioTotal,
                fechaCreacion = System.currentTimeMillis()
            )
        )

        val citaServiciosList = servicioIds.map { sId ->
            CitaServicio(citaId = citaId.toInt(), servicioId = sId)
        }
        citaServicios.insertarTodos(citaServiciosList)

        val nombresServicios = serviciosList.joinToString(", ") { it.nombre }
        notificaciones.insertar(
            Notificacion(
                usuarioId = mascota.duenoId,
                titulo = "Cita solicitada",
                mensaje = "Tu cita para ${mascota.nombre} ($nombresServicios) ha sido solicitada para el $fecha a las $hora.",
                fecha = LocalDate.now().toString(),
                leida = false,
                tipo = "SOLICITADA"
            )
        )

        citaId
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

    override suspend fun cambiarEstadoCita(id: Int, estado: String) {
        db.withTransaction {
            val cita = citas.obtener(id) ?: return@withTransaction
            citas.actualizarEstado(id, estado)

            val mascota = mascotas.obtener(cita.mascotaId) ?: return@withTransaction
            val servicio = servicios.obtener(cita.servicioId)
            val servicioNombre = servicio?.nombre ?: "Servicio"

            val (titulo, tipo) = when (estado) {
                "CONFIRMADA" -> "Cita confirmada" to "CONFIRMADA"
                "CANCELADA" -> "Cita cancelada" to "CANCELADA"
                "ATENDIDA" -> "Cita atendida" to "ATENDIDA"
                else -> "Actualización de cita" to "ACTUALIZACION"
            }

            val mensaje = when (estado) {
                "CONFIRMADA" -> "Tu cita para ${mascota.nombre} ($servicioNombre) ha sido confirmada para el ${cita.fecha} a las ${cita.hora}."
                "CANCELADA" -> "Tu cita para ${mascota.nombre} ($servicioNombre) programada para el ${cita.fecha} ha sido cancelada."
                "ATENDIDA" -> "La cita de ${mascota.nombre} ($servicioNombre) ha sido atendida con éxito."
                else -> "El estado de tu cita para ${mascota.nombre} ($servicioNombre) ha cambiado a $estado."
            }

            notificaciones.insertar(
                Notificacion(
                    usuarioId = mascota.duenoId,
                    titulo = titulo,
                    mensaje = mensaje,
                    fecha = LocalDate.now().toString(),
                    leida = false,
                    tipo = tipo
                )
            )
        }
    }

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

            val mascota = mascotas.obtener(cita.mascotaId) ?: return@withTransaction
            val servicio = servicios.obtener(cita.servicioId)
            val servicioNombre = servicio?.nombre ?: "Servicio"

            notificaciones.insertar(
                Notificacion(
                    usuarioId = mascota.duenoId,
                    titulo = "Cita atendida",
                    mensaje = "La cita de ${mascota.nombre} ($servicioNombre) ha sido atendida con éxito.",
                    fecha = LocalDate.now().toString(),
                    leida = false,
                    tipo = "ATENDIDA"
                )
            )
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

    override suspend fun reprogramarCita(id: Int, fecha: String, hora: String) {
        db.withTransaction {
            citas.reprogramarCita(id, fecha, hora)
            val cita = citas.obtener(id) ?: return@withTransaction
            val mascota = mascotas.obtener(cita.mascotaId) ?: return@withTransaction
            val servicio = servicios.obtener(cita.servicioId)
            val servicioNombre = servicio?.nombre ?: "Servicio"

            notificaciones.insertar(
                Notificacion(
                    usuarioId = mascota.duenoId,
                    titulo = "Cita reprogramada",
                    mensaje = "Tu cita para ${mascota.nombre} ($servicioNombre) ha sido reprogramada para el $fecha a las $hora.",
                    fecha = LocalDate.now().toString(),
                    leida = false,
                    tipo = "REPROGRAMADA"
                )
            )
        }
    }

    override suspend fun contarOcupadasExcluyendo(fecha: String, hora: String, citaId: Int): Int =
        citas.contarOcupadasExcluyendo(fecha, hora, citaId)

    override fun observarNotificaciones(usuarioId: Int): Flow<List<Notificacion>> =
        notificaciones.observarPorUsuario(usuarioId)

    override fun contarNotificacionesNoLeidas(usuarioId: Int): Flow<Int> =
        notificaciones.contarNoLeidas(usuarioId)

    override suspend fun marcarNotificacionLeida(id: Int) =
        notificaciones.marcarComoLeida(id)

    override suspend fun marcarTodasNotificacionesLeidas(usuarioId: Int) =
        notificaciones.marcarTodasComoLeidas(usuarioId)

    override suspend fun eliminarNotificacion(id: Int) =
        notificaciones.eliminar(id)

    override fun observarServiciosDeCita(citaId: Int): Flow<List<Servicio>> =
        citaServicios.observarServiciosDeCita(citaId)

    override fun observarUsuarios(): Flow<List<Usuario>> =
        usuarios.observarTodos()

    override fun observarTodasCitas(): Flow<List<CitaConDetalle>> =
        citas.observarTodas()
}
