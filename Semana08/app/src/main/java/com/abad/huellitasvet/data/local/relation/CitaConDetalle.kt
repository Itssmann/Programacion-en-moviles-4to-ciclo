package com.abad.huellitasvet.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.entity.Servicio

data class CitaConDetalle(
    @Embedded
    val cita: Cita,

    @Relation(
        entity = Mascota::class,
        parentColumn = "mascotaId",
        entityColumn = "id"
    )
    val mascota: MascotaConDueno,

    @Relation(
        parentColumn = "servicioId",
        entityColumn = "id"
    )
    val servicio: Servicio
)

