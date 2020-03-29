package me.vendoor.dragonball.api.migration

interface MigrationScript {
    fun getVersion(): String

    fun getDescription(): String

    fun perform(context: MigrationContext)
}