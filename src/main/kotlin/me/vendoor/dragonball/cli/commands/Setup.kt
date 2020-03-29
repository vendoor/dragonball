package me.vendoor.dragonball.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import me.vendoor.dragonball.api.setup.SetupPerformer
import me.vendoor.dragonball.specification.vendoorDatabaseSpecification
import me.vendoor.dragonball.cli.util.loadConfigurationFrom
import java.io.File

class Setup: CliktCommand(
    help = "Initializes an empty database."
) {
    val configFile: File by option(
            help = "Path to the configuration file."
    ).file().required()

    override fun run() {
        val configuration = loadConfigurationFrom(configFile)

        val client = obtainClient(configuration.database.connectionString)

        val databaseSpecification = vendoorDatabaseSpecification()

        SetupPerformer(configuration, client).setupDatabaseFromSpecification(databaseSpecification)

        client.close()
    }

    private fun obtainClient(connectionString: String): MongoClient {
        val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(connectionString))
                .build();

        return MongoClients.create(settings)
    }
}
