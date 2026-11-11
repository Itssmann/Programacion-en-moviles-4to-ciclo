package com.abad.huellitasvet.data.local.dao

import androidx.room.*
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.relation.MascotaConDueno
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {

    @Insert
    suspend fun insertar(mascota: Mascota): Long

    @Insert
    suspend fun insertarTodos(lista: List<Mascota>)

    @Update
    suspend fun actualizar(mascota: Mascota)

    @Delete
    suspend fun eliminar(mascota: Mascota)

    @Query("UPDATE mascotas SET peso = :peso WHERE id = :id")
    suspend fun actualizarPeso(id: Int, peso: Double)

    @Transaction
    @Query("SELECT * FROM mascotas WHERE duenoId = :duenoId ORDER BY nombre")
    fun observarPorDueno(duenoId: Int): Flow<List<MascotaConDueno>>

    @Transaction
    @Query("SELECT * FROM mascotas WHERE id = :id")
    fun observarUna(id: Int): Flow<MascotaConDueno?>

    @Transaction
    @Query("SELECT * FROM mascotas ORDER BY nombre")
    fun observarTodas(): Flow<List<MascotaConDueno>>

    @Query("SELECT COUNT(*) FROM citas WHERE mascotaId = :mascotaId")
    suspend fun contarCitas(mascotaId: Int): Int

    @Query("SELECT * FROM mascotas WHERE id = :id")
    suspend fun obtener(id: Int): Mascota?
}
