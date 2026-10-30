package com.abad.huellitasvet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abad.huellitasvet.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Insert
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun observarTodos(): Flow<List<Usuario>>
}
