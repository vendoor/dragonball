package me.vendoor.dragonball.library.migration

abstract class MigrationScript(
        val version: String,
        val description: String
) {
    abstract fun migrate(context: MigrationContext)
}