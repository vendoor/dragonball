package me.vendoor.dragonball.api.migration

abstract class MigrationScript {
    init {
        // No worries about the leak.
        MigrationScriptRegistry.registerMigrationScript(this)
    }

    abstract fun getVersion(): String

    abstract fun getDescription(): String

    abstract fun migrate(context: MigrationContext)
}