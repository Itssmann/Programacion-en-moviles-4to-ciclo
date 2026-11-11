package com.abad.huellitasvet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abad.huellitasvet.data.local.entity.CitaServicio
import com.abad.huellitasvet.data.local.entity.Servicio
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaServicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(citaServicio: CitaServicio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(lista: List<CitaServicio>)

    @Query("""
        SELECT s.* FROM servicios s
        INNER JOIN cita_servicios cs ON cs.servicioId = s.id
        WHERE cs.citaId = :citaId
    """)
    suspend fun obtenerServiciosDeCita(citaId: Int): List<Servicio>

    @Query("""
        SELECT s.* FROM servicios s
        INNER JOIN cita_servicios cs ON cs.servicioId = s.id
        WHERE cs.citaId = :citaId
    """)
    fun observarServiciosDeCita(citaId: Int): Flow<List<Servicio>>

    @Query("DELETE FROM cita_servicios WHERE citaId = :citaId")
    suspend fun eliminarPorCita(citaId: Int)
}
