package com.abad.huellitasvet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.abad.huellitasvet.data.local.dao.CitaDao
import com.abad.huellitasvet.data.local.dao.CitaServicioDao
import com.abad.huellitasvet.data.local.dao.MascotaDao
import com.abad.huellitasvet.data.local.dao.NotificacionDao
import com.abad.huellitasvet.data.local.dao.ServicioDao
import com.abad.huellitasvet.data.local.dao.UsuarioDao
import com.abad.huellitasvet.data.local.entity.Cita
import com.abad.huellitasvet.data.local.entity.CitaServicio
import com.abad.huellitasvet.data.local.entity.Mascota
import com.abad.huellitasvet.data.local.entity.Notificacion
import com.abad.huellitasvet.data.local.entity.Servicio
import com.abad.huellitasvet.data.local.entity.Usuario

@Database(
    entities = [
        Servicio::class,
        Usuario::class,
        Mascota::class,
        Cita::class,
        Notificacion::class,
        CitaServicio::class
    ],
    version = 4,
    exportSchema = false
)
abstract class VetDatabase : RoomDatabase() {

    abstract fun servicioDao(): ServicioDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun mascotaDao(): MascotaDao
    abstract fun citaDao(): CitaDao
    abstract fun notificacionDao(): NotificacionDao
    abstract fun citaServicioDao(): CitaServicioDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_correo ON usuarios(correo)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS notificaciones (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        usuarioId INTEGER NOT NULL,
                        titulo TEXT NOT NULL,
                        mensaje TEXT NOT NULL,
                        fecha TEXT NOT NULL,
                        leida INTEGER NOT NULL DEFAULT 0,
                        tipo TEXT NOT NULL,
                        FOREIGN KEY(usuarioId) REFERENCES usuarios(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notificaciones_usuarioId ON notificaciones(usuarioId)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE usuarios ADD COLUMN telefono TEXT")
                database.execSQL("ALTER TABLE usuarios ADD COLUMN distrito TEXT")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cita_servicios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        citaId INTEGER NOT NULL,
                        servicioId INTEGER NOT NULL,
                        FOREIGN KEY(citaId) REFERENCES citas(id) ON DELETE CASCADE,
                        FOREIGN KEY(servicioId) REFERENCES servicios(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_cita_servicios_citaId ON cita_servicios(citaId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_cita_servicios_servicioId ON cita_servicios(servicioId)")
                database.execSQL("""
                    INSERT INTO cita_servicios (citaId, servicioId)
                    SELECT id, servicioId FROM citas WHERE servicioId IS NOT NULL
                """)
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build().also { INSTANCE = it }
            }
    }
}
