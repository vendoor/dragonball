package me.vendoor.dragonball.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.typesafe.config.ConfigFactory
import me.vendoor.dragonball.specification.vendoorDatabaseSpecification
import me.vendoor.dragonball.dsl.setupDatabaseFromSpecification
import me.vendoor.dragonball.migration.api.MigrationPerformer
import java.io.File

class Migrate: CliktCommand(
        help = "Migrates an initialized database."
) {
    val configFile: File by option(
            help = "Path to the configuration file."
    ).file().required()

    val targetVersion: String by option(
            help = "The target version of the migration"
    ).required()

    override fun run() {
        val config = ConfigFactory.parseFile(configFile)

        val client = obtainClient(config.getString("database.connectionString"))

        MigrationPerformer.perform(targetVersion, client)

        client.close()
    }

    private fun obtainClient(connectionString: String): MongoClient {
        val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(connectionString))
                .build();

        return MongoClients.create(settings)
    }
}
