package com.abad.huellitasvet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cita_servicios",
    foreignKeys = [
        ForeignKey(
            entity = Cita::class,
            parentColumns = ["id"],
            childColumns = ["citaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Servicio::class,
            parentColumns = ["id"],
            childColumns = ["servicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("citaId"),
        Index("servicioId")
    ]
)
data class CitaServicio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val citaId: Int,
    val servicioId: Int
)
