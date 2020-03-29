package me.vendoor.dragonball.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.typesafe.config.ConfigFactory
import me.vendoor.dragonball.api.migration.MigrationPerformer
import me.vendoor.dragonball.cli.util.loadConfigurationFrom
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
        val configuration = loadConfigurationFrom(configFile)

        val client = obtainClient(configuration.database.connectionString)
        val database = client.getDatabase(configuration.database.name)

        val performer = MigrationPerformer(configuration, database)

        client.close()
    }

    private fun obtainClient(connectionString: String): MongoClient {
        val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(connectionString))
                .build();

        return MongoClients.create(settings)
    }
}
