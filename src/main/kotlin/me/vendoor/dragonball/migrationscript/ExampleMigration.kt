package me.vendoor.dragonball.migrationscript

import me.vendoor.dragonball.api.migration.MigrationContext
import me.vendoor.dragonball.api.migration.MigrationScript

class ExampleMigration : MigrationScript() {
    override fun getVersion() = "1.1.0"

    override fun getDescription() = """
        * Removing the User collection. And all of its data. Why not?
        * Creating an empty Customer collection.
    """.trimIndent()

    override fun migrate(context: MigrationContext) {
        context.drop {
            collection { "User" }
        }

        context.create {
            collection {
                name { "Customer" }
            }
        }
    }
}