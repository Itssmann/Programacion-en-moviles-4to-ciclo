package com.abad.huellitasvet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.abad.huellitasvet.data.local.dao.CitaDao
import com.abad.huellitasvet.data.local.dao.MascotaDao
import com.abad.huellitasvet.data.local.dao.ServicioDao
import com.abad.huellitasvet.data.local.dao.UsuarioDao
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.data.local.entity.Usuario

@Database(
    entities = [
        Servicio::class,
        Usuario::class,
        Mascota::class,
        Cita::class
    ],
    version = 2,
    exportSchema = false
)
abstract class VetDatabase : RoomDatabase() {

    abstract fun servicioDao(): ServicioDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun mascotaDao(): MascotaDao
    abstract fun citaDao(): CitaDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_correo ON usuarios(correo)")
            }
        }

        @Volatile
        private var INSTANCE: VetDatabase? = null

        fun obtener(context: Context): VetDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VetDatabase::class.java,
                    "huellitasvet.db"
                ).addMigrations(MIGRATION_1_2)
                .build().also { INSTANCE = it }
            }
    }
}
