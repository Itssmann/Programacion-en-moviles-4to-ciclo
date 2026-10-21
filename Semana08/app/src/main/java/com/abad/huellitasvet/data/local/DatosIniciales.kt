package com.abad.huellitasvet.data.local

import androidx.room.withTransaction
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.data.local.entity.Usuario
import java.time.LocalDate

object DatosIniciales {

    suspend fun sembrar(db: VetDatabase) {
        db.withTransaction {
            if (db.usuarioDao().contar() > 0) return@withTransaction

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
                    Usuario(1, "Ana Torres", "ana@correo.test", "1234", "CLIENTE"),
                    Usuario(2, "Luis Perez", "luis@correo.test", "1234", "CLIENTE"),
                    Usuario(3, "Veterinario", "vet@huellitas.test", "admin", "ADMIN")
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
        }
    }
}

