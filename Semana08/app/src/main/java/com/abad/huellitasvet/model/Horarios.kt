package com.abad.huellitasvet.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Horarios {
    val horas: List<String> = (0 until 18).map { indice ->
        LocalTime.of(9, 0)
            .plusMinutes(indice * 30L)
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}

