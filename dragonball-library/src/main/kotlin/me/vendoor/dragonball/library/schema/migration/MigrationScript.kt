package me.vendoor.dragonball.library.schema.migration

import me.vendoor.dragonball.library.migration.MigrationContext

abstract class MigrationScript(
        val version: String,
        val description: String
) {
    abstract fun migrate(context: MigrationContext)
}