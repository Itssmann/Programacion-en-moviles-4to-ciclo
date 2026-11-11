package com.abad.pokedex.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `favoritos_usuario` (
                `uid` TEXT NOT NULL,
                `pokemonNombre` TEXT NOT NULL,
                PRIMARY KEY(`uid`, `pokemonNombre`),
                FOREIGN KEY(`pokemonNombre`)
                    REFERENCES `pokemones`(`nombre`)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
            `index_favoritos_usuario_pokemonNombre`
            ON `favoritos_usuario` (`pokemonNombre`)
            """.trimIndent()
        )
    }
}
