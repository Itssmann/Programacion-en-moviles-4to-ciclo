package com.abad.huellitasvet.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"

    const val INICIO = "inicio"
    const val MASCOTAS = "mascotas"
    const val FORMULARIO_MASCOTA = "formulario_mascota"
    const val FICHA_MASCOTA = "ficha_mascota/{mascotaId}"
    const val PEDIR_CITA = "pedir_cita"
    const val MIS_CITAS = "mis_citas"
    const val PERFIL = "perfil"
    const val NOTIFICACIONES = "notificaciones"

    const val PANEL = "panel"
    const val AGENDA = "agenda"
    const val ATENDER_CITA = "atender_cita/{citaId}"
    const val PACIENTES = "pacientes"
    const val SERVICIOS = "servicios"
    const val FORMULARIO_SERVICIO = "formulario_servicio"
    const val AJUSTES = "ajustes"

    const val ADMIN_DASHBOARD = "admin_dashboard"

    fun fichaMascota(id: Int) = "ficha_mascota/$id"
    fun atenderCita(id: Int) = "atender_cita/$id"
}
