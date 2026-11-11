package com.abad.huellitasvet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abad.huellitasvet.data.local.entity.Notificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Query("SELECT * FROM notificaciones WHERE usuarioId = :usuarioId ORDER BY id DESC")
    fun observarPorUsuario(usuarioId: Int): Flow<List<Notificacion>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE usuarioId = :usuarioId AND leida = 0")
    fun contarNoLeidas(usuarioId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(notificacion: Notificacion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(lista: List<Notificacion>)

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarComoLeida(id: Int)

    @Query("UPDATE notificaciones SET leida = 1 WHERE usuarioId = :usuarioId")
    suspend fun marcarTodasComoLeidas(usuarioId: Int)

    @Query("DELETE FROM notificaciones WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Query("SELECT COUNT(*) FROM notificaciones WHERE usuarioId = :usuarioId")
    suspend fun contarPorUsuario(usuarioId: Int): Int
}
