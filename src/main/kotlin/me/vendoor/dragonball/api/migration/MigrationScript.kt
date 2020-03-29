package me.vendoor.dragonball.api.migration

abstract class MigrationScript(
        val version: String,
        val description: String
) {
    abstract fun migrate(context: MigrationContext)
}