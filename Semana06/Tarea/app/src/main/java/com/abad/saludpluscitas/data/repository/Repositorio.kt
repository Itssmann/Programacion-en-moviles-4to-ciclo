package com.abad.saludpluscitas.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.abad.saludpluscitas.R
import com.abad.saludpluscitas.data.model.Cita
import com.abad.saludpluscitas.data.model.Especialidad
import com.abad.saludpluscitas.data.model.Medico
import com.abad.saludpluscitas.data.model.Sede
import com.abad.saludpluscitas.data.model.Usuario
import java.time.LocalDate
import java.time.LocalTime

data class InfoCitaRegistrada(
    val medicoNombre: String,
    val especialidad: String,
    val sede: String,
    val fecha: String,
    val hora: String
)

object Repositorio {

    val usuarios = mutableListOf(
        Usuario(1, "Luis Abad", "luis@gmail.com", "987654321", "123456")
    )

    var usuarioActual by mutableStateOf<Usuario?>(null)

    val sedes = listOf(
        Sede(1, "Jesús María", "Jesús María", "Av. Garcés de la Vega 142", "Frente al Campo de Marte (Dato de prueba)"),
        Sede(2, "Ate", "Ate", "Av. La Molina 789", "Cruce con Av. Javier Prado (Dato de prueba)"),
        Sede(3, "Santa Anita", "Santa Anita", "Av. Los Eucaliptos 321", "Cerca al Mall Aventura (Dato de prueba)"),
        Sede(4, "San Borja", "San Borja", "Av. San Borja Sur 456", "A dos cuadras de la Estación La Cultura (Dato de prueba)"),
        Sede(5, "Miraflores", "Miraflores", "Av. Arequipa 4321", "Cerca al Óvalo 2 de Mayo (Dato de prueba)")
    )

    var sedeActual by mutableStateOf<Sede?>(null)
    var ultimaCitaRegistrada by mutableStateOf<InfoCitaRegistrada?>(null)

    val especialidades = listOf(
        Especialidad(1, "Medicina General", "Atencion medica general"),
        Especialidad(2, "Pediatria", "Atencion para niños"),
        Especialidad(3, "Ginecologia", "Salud de la mujer"),
        Especialidad(4, "Cardiologia", "Enfermedades del corazon"),
        Especialidad(5, "Dermatologia", "Cuidado de la piel"),
        Especialidad(6, "Odontologia", "Salud dental")
    )

    val medicos = listOf(
        // Medicina General (1)
        Medico(1, "Dr. Carlos Ruiz", 1, 8, 4.8, R.drawable.ic_launcher_foreground),
        Medico(2, "Dr. Jorge Gómez", 1, 9, 4.7, R.drawable.ic_launcher_foreground),
        // Pediatria (2)
        Medico(3, "Dra. Claudia Rios", 2, 12, 4.9, R.drawable.ic_launcher_foreground),
        Medico(4, "Dra. Sofía Vargas", 2, 6, 4.6, R.drawable.ic_launcher_foreground),
        // Ginecologia (3)
        Medico(5, "Dra. Ana Torres", 3, 10, 4.9, R.drawable.ic_launcher_foreground),
        Medico(6, "Dr. Ricardo Silva", 3, 11, 4.8, R.drawable.ic_launcher_foreground),
        // Cardiologia (4)
        Medico(7, "Dr. Luis Ramirez", 4, 7, 4.7, R.drawable.ic_launcher_foreground),
        Medico(8, "Dra. Carmen Mendoza", 4, 9, 4.9, R.drawable.ic_launcher_foreground),
        // Dermatologia (5)
        Medico(9, "Dra. Maria Soto", 5, 9, 4.8, R.drawable.ic_launcher_foreground),
        Medico(10, "Dr. Roberto Pérez", 5, 5, 4.5, R.drawable.ic_launcher_foreground),
        // Odontologia (6)
        Medico(11, "Dr. Pedro Lopez", 6, 6, 4.6, R.drawable.ic_launcher_foreground),
        Medico(12, "Dra. Elena Castro", 6, 8, 4.7, R.drawable.ic_launcher_foreground)
    )

    val citas = mutableStateListOf<Cita>()

    fun obtenerEspecialidades(): List<Especialidad> {
        return especialidades
    }

    fun obtenerMedicosPorEspecialidad(especialidadId: Int): List<Medico> {
        return medicos.filter { it.especialidadId == especialidadId }
    }

    fun obtenerMedicoPorId(id: Int): Medico? {
        return medicos.find { it.id == id }
    }

    fun registrarUsuario(usuario: Usuario) {
        usuarios.add(usuario)
        usuarioActual = usuario
    }

    fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        val u = usuarios.find {
            it.correo == correo && it.contrasena == contrasena
        }
        if (u != null) {
            usuarioActual = u
        }
        return u
    }

    fun cerrarSesion() {
        usuarioActual = null
        sedeActual = null
        ultimaCitaRegistrada = null
    }

    fun estaHorarioOcupado(medicoId: Int, fecha: String, hora: String): Boolean {
        return citas.any {
            it.medicoId == medicoId &&
                    it.fecha.trim() == fecha.trim() &&
                    it.hora.trim() == hora.trim() &&
                    it.estado.equals("Confirmada", true)
        }
    }

    fun registrarCita(cita: Cita): Boolean {
        if (estaHorarioOcupado(cita.medicoId, cita.fecha, cita.hora)) {
            return false
        }
        citas.add(cita)
        return true
    }

    fun cancelarCita(citaId: Int, usuarioId: Int): Boolean {
        val index = citas.indexOfFirst { it.id == citaId && it.usuarioId == usuarioId }
        if (index != -1) {
            val cita = citas[index]
            if (cita.estado.equals("Cancelada", true)) return false
            if (esCitaPasada(cita.fecha, cita.hora)) return false
            citas[index] = cita.copy(estado = "Cancelada")
            return true
        }
        return false
    }

    fun esCitaPasada(fechaIso: String, horaStr: String): Boolean {
        return try {
            val localDate = LocalDate.parse(fechaIso)
            val partes = horaStr.split(":")
            val horaInt = partes[0].toInt()
            val minInt = partes[1].toInt()
            val localTime = LocalTime.of(horaInt, minInt)
            val now = java.time.LocalDateTime.now()
            val citaDateTime = java.time.LocalDateTime.of(localDate, localTime)
            citaDateTime.isBefore(now)
        } catch (_: Exception) {
            false
        }
    }

    fun obtenerCitasPorUsuario(usuarioId: Int): List<Cita> {
        return citas.filter { it.usuarioId == usuarioId }
    }
}
