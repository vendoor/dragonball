package me.vendoor.dragonball.api.migration

object MigrationScriptRegistry {
    private val scripts: MutableList<MigrationScript> = ArrayList()

    fun getRegisteredScripts(): List<MigrationScript> =
            scripts

    fun registerMigrationScript(script: MigrationScript) {
        scripts.add(script)
    }
}