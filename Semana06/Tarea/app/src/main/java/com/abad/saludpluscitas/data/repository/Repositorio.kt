package com.abad.saludpluscitas.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.abad.saludpluscitas.data.model.Cita
import com.abad.saludpluscitas.data.model.Especialidad
import com.abad.saludpluscitas.data.model.Medico
import com.abad.saludpluscitas.data.model.Usuario

object Repositorio {

    val usuarios = mutableListOf(
        Usuario(1, "Luis Abad", "luis@gmail.com", "987654321", "123456")
    )

    var usuarioActual by mutableStateOf<Usuario?>(null)

    val especialidades = listOf(
        Especialidad(1, "Medicina General", "Atencion medica general"),
        Especialidad(2, "Pediatria", "Atencion para niños"),
        Especialidad(3, "Ginecologia", "Salud de la mujer"),
        Especialidad(4, "Cardiologia", "Enfermedades del corazon"),
        Especialidad(5, "Dermatologia", "Cuidado de la piel"),
        Especialidad(6, "Odontologia", "Salud dental")
    )

    val medicos = listOf(
        Medico(1, "Dra. Ana Torres", 3, 10, 4.9),
        Medico(2, "Dr. Carlos Ruiz", 1, 8, 4.8),
        Medico(3, "Dra. Claudia Rios", 2, 12, 4.9),
        Medico(4, "Dr. Luis Ramirez", 4, 7, 4.7),
        Medico(5, "Dra. Maria Soto", 5, 9, 4.8),
        Medico(6, "Dr. Pedro Lopez", 6, 6, 4.6)
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
    }

    fun registrarCita(cita: Cita) {
        citas.add(cita)
    }

    fun obtenerCitasPorUsuario(usuarioId: Int): List<Cita> {
        return citas.filter { it.usuarioId == usuarioId }
    }

    fun estaHorarioOcupado(medicoId: Int, fecha: String, hora: String): Boolean {
        return citas.any {
            it.medicoId == medicoId &&
                    it.fecha == fecha &&
                    it.hora == hora &&
                    it.estado != "Cancelada"
        }
    }
}
