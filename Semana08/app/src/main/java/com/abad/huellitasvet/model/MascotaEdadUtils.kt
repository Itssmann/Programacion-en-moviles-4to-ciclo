package com.abad.huellitasvet.model

import java.time.LocalDate
import java.time.Period

object MascotaEdadUtils {
    fun calcularEdadTexto(anioNacimiento: Int): String {
        val fechaNacimiento = if (anioNacimiento > 3000) {
            LocalDate.ofEpochDay(anioNacimiento.toLong())
        } else {
            LocalDate.of(anioNacimiento, 1, 1)
        }

        val hoy = LocalDate.now()
        if (fechaNacimiento.isAfter(hoy)) return "Recién nacido"

        val periodo = Period.between(fechaNacimiento, hoy)
        val anos = periodo.years
        val meses = periodo.months
        val dias = periodo.days

        return when {
            anos > 0 -> {
                if (meses > 0) "$anos ${if (anos == 1) "año" else "años"} y $meses ${if (meses == 1) "mes" else "meses"}"
                else "$anos ${if (anos == 1) "año" else "años"}"
            }
            meses > 0 -> "$meses ${if (meses == 1) "mes" else "meses"}"
            else -> "$dias ${if (dias == 1) "día" else "días"}"
        }
    }

    fun parseFechaNacimiento(anioNacimiento: Int): LocalDate {
        return if (anioNacimiento > 3000) {
            LocalDate.ofEpochDay(anioNacimiento.toLong())
        } else {
            LocalDate.of(anioNacimiento, 1, 1)
        }
    }

    fun toStoredValue(fecha: LocalDate): Int {
        return fecha.toEpochDay().toInt()
    }
}
