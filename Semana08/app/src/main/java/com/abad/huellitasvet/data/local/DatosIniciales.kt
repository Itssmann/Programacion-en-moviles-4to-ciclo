package com.abad.huellitasvet.data.local

import androidx.room.withTransaction
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.data.local.entity.CitaServicio
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.entity.Notificacion
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.data.local.entity.Usuario
import java.time.LocalDate

object DatosIniciales {

    suspend fun sembrar(db: VetDatabase) {
        db.withTransaction {
            if (db.usuarioDao().contar() == 0) {
                db.servicioDao().insertarTodos(
                    listOf(
                        Servicio(1, "Consulta general", "Evaluacion general de salud", 30, 60.0, true, "heart"),
                        Servicio(2, "Vacunacion", "Aplicacion de vacunas", 30, 45.0, true, "syringe"),
                        Servicio(3, "Baño y corte", "Higiene y cuidado estetico", 60, 50.0, true, "scissors"),
                        Servicio(4, "Desparasitacion", "Control de parasitos", 15, 25.0, false, "heart")
                    )
                )

                db.usuarioDao().insertarTodos(
                    listOf(
                        Usuario(1, "Ana Torres", "ana@correo.test", "1234", "CLIENTE", "987654321", "Miraflores"),
                        Usuario(2, "Luis Perez", "luis@correo.test", "1234", "CLIENTE", "912345678", "San Isidro"),
                        Usuario(3, "Veterinario", "vet@huellitas.test", "admin", "VET", "998877665", "Surco"),
                        Usuario(4, "Administrador", "admin@huellitas.test", "admin123", "ADMIN", "999000111", "Lima")
                    )
                )

                db.mascotaDao().insertarTodos(
                    listOf(
                        Mascota(1, 1, "Luna", "PERRO", "Labrador", 2020, 18.5, "HEMBRA"),
                        Mascota(2, 1, "Max", "PERRO", "Beagle", 2022, 11.0, "MACHO"),
                        Mascota(3, 2, "Rocky", "PERRO", "Mestizo", 2019, 21.0, "MACHO"),
                        Mascota(4, 2, "Michi", "GATO", "Siames", 2023, 4.2, "HEMBRA")
                    )
                )

                val hoy = LocalDate.now()
                val ahora = System.currentTimeMillis()

                db.citaDao().insertarTodos(
                    listOf(
                        Cita(1, 1, 1, hoy.minusDays(15).toString(), "09:00",
                            "Control general", "ATENDIDA", 60.0,
                            "Paciente saludable", "Continuar controles", 18.2, ahora),

                        Cita(2, 4, 2, hoy.minusDays(10).toString(), "10:00",
                            "Vacuna anual", "ATENDIDA", 45.0,
                            "Vacunacion realizada", "Observar durante 24 horas", 4.1, ahora),

                        Cita(3, 3, 1, hoy.minusDays(5).toString(), "11:00",
                            "Revision", "ATENDIDA", 60.0,
                            "Revision sin complicaciones", "Control en seis meses", 20.8, ahora),

                        Cita(4, 2, 3, hoy.minusDays(2).toString(), "14:00",
                            "Baño", "ATENDIDA", 50.0,
                            "Servicio completado", "Cepillado frecuente", 11.0, ahora),

                        Cita(5, 1, 2, hoy.toString(), "09:00",
                            "Vacuna pendiente", "CONFIRMADA", 45.0),

                        Cita(6, 3, 1, hoy.toString(), "11:00",
                            "Consulta", "PENDIENTE", 60.0),

                        Cita(7, 4, 3, hoy.plusDays(1).toString(), "10:00",
                            "Baño y corte", "PENDIENTE", 50.0),

                        Cita(8, 2, 1, hoy.plusDays(2).toString(), "15:00",
                            "Chequeo", "CONFIRMADA", 60.0),

                        Cita(9, 1, 3, hoy.plusDays(3).toString(), "12:00",
                            "Servicio cancelado", "CANCELADA", 50.0),

                        Cita(10, 3, 2, hoy.plusDays(4).toString(), "16:00",
                            "Vacunacion", "PENDIENTE", 45.0)
                    )
                )

                db.citaServicioDao().insertarTodos(
                    listOf(
                        CitaServicio(1, 1, 1),
                        CitaServicio(2, 2, 2),
                        CitaServicio(3, 3, 1),
                        CitaServicio(4, 4, 3),
                        CitaServicio(5, 5, 2),
                        CitaServicio(6, 6, 1),
                        CitaServicio(7, 7, 3),
                        CitaServicio(8, 8, 1),
                        CitaServicio(9, 9, 3),
                        CitaServicio(10, 10, 2)
                    )
                )

                db.notificacionDao().insertarTodas(
                    listOf(
                        Notificacion(
                            usuarioId = 1,
                            titulo = "Cita atendida",
                            mensaje = "La cita de Luna (Control general) del ${hoy.minusDays(15)} ha sido atendida con éxito.",
                            fecha = hoy.minusDays(15).toString(),
                            leida = true,
                            tipo = "ATENDIDA"
                        ),
                        Notificacion(
                            usuarioId = 2,
                            titulo = "Cita atendida",
                            mensaje = "La cita de Michi (Vacunacion) del ${hoy.minusDays(10)} ha sido atendida con éxito.",
                            fecha = hoy.minusDays(10).toString(),
                            leida = true,
                            tipo = "ATENDIDA"
                        ),
                        Notificacion(
                            usuarioId = 2,
                            titulo = "Cita atendida",
                            mensaje = "La cita de Rocky (Revision) del ${hoy.minusDays(5)} ha sido atendida con éxito.",
                            fecha = hoy.minusDays(5).toString(),
                            leida = true,
                            tipo = "ATENDIDA"
                        ),
                        Notificacion(
                            usuarioId = 1,
                            titulo = "Cita atendida",
                            mensaje = "La cita de Max (Baño) del ${hoy.minusDays(2)} ha sido atendida con éxito.",
                            fecha = hoy.minusDays(2).toString(),
                            leida = true,
                            tipo = "ATENDIDA"
                        ),
                        Notificacion(
                            usuarioId = 1,
                            titulo = "Cita confirmada",
                            mensaje = "Tu cita para Luna (Vacunacion) ha sido confirmada para hoy a las 09:00.",
                            fecha = hoy.toString(),
                            leida = false,
                            tipo = "CONFIRMADA"
                        ),
                        Notificacion(
                            usuarioId = 2,
                            titulo = "Cita solicitada",
                            mensaje = "Tu cita para Rocky (Consulta general) ha sido solicitada para hoy a las 11:00.",
                            fecha = hoy.toString(),
                            leida = false,
                            tipo = "SOLICITADA"
                        ),
                        Notificacion(
                            usuarioId = 2,
                            titulo = "Cita solicitada",
                            mensaje = "Tu cita para Michi (Baño y corte) ha sido solicitada para el ${hoy.plusDays(1)} a las 10:00.",
                            fecha = hoy.toString(),
                            leida = false,
                            tipo = "SOLICITADA"
                        ),
                        Notificacion(
                            usuarioId = 1,
                            titulo = "Cita confirmada",
                            mensaje = "Tu cita para Max (Consulta general) ha sido confirmada para el ${hoy.plusDays(2)} a las 15:00.",
                            fecha = hoy.toString(),
                            leida = false,
                            tipo = "CONFIRMADA"
                        ),
                        Notificacion(
                            usuarioId = 1,
                            titulo = "Cita cancelada",
                            mensaje = "Tu cita para Luna (Baño y corte) programada para el ${hoy.plusDays(3)} ha sido cancelada.",
                            fecha = hoy.toString(),
                            leida = false,
                            tipo = "CANCELADA"
                        ),
                        Notificacion(
                            usuarioId = 2,
                            titulo = "Cita solicitada",
                            mensaje = "Tu cita para Rocky (Vacunacion) ha sido solicitada para el ${hoy.plusDays(4)} a las 16:00.",
                            fecha = hoy.toString(),
                            leida = false,
                            tipo = "SOLICITADA"
                        )
                    )
                )
            }

            // Ensure admin account always exists even if database already existed previously
            val adminUser = db.usuarioDao().obtenerPorCorreo("admin@huellitas.test")
            if (adminUser == null) {
                db.usuarioDao().insertar(
                    Usuario(
                        nombre = "Administrador",
                        correo = "admin@huellitas.test",
                        clave = "admin123",
                        rol = "ADMIN",
                        telefono = "999000111",
                        distrito = "Lima"
                    )
                )
            }
        }
    }
}
