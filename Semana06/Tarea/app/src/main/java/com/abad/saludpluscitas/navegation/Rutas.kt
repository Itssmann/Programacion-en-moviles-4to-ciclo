package com.abad.saludpluscitas.navigation

sealed class Rutas(val ruta: String) {

    object Splash : Rutas("splash")
    object Registro : Rutas("registro")
    object Login : Rutas("login")
    object Terminos : Rutas("terminos")

    object Inicio : Rutas("inicio")
    object Especialidades : Rutas("especialidades")
    object Medicos : Rutas("medicos/{especialidadId}")
    object FechaHora : Rutas("fechaHora/{medicoId}")
    object ConfirmarCita : Rutas("confirmar/{medicoId}/{fecha}/{hora}")
    object CitaExitosa : Rutas("citaExitosa")

    object MisCitas : Rutas("misCitas")
    object DetalleCita : Rutas("detalleCita/{citaId}")

    object Perfil : Rutas("perfil")
    object Resultados : Rutas("resultados")
    object Notificaciones : Rutas("notificaciones")
}