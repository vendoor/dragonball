package me.vendoor.dragonball.schema.migration

import me.vendoor.dragonball.library.migration.MigrationContext

abstract class MigrationScript(
        val version: String,
        val description: String
) {
    abstract fun migrate(context: MigrationContext)
}