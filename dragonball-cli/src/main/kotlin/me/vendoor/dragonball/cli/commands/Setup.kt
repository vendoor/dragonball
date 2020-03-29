package me.vendoor.dragonball.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import me.vendoor.dragonball.library.setup.SetupPerformer
import me.vendoor.dragonball.common.database.openClient
import me.vendoor.dragonball.specification.vendoorDatabaseSpecification
import me.vendoor.dragonball.cli.util.loadConfigurationFrom
import java.io.File

class Setup: CliktCommand(
    help = "Initializes an empty database."
) {
    private val configFile: File by option(
            help = "Path to the configuration file."
    ).file().required()

    override fun run() {
        val configuration = loadConfigurationFrom(configFile)

        val client = openClient(configuration.database.connectionString)

        val databaseSpecification = vendoorDatabaseSpecification()

        SetupPerformer(configuration, client).setupDatabaseFromSpecification(databaseSpecification)

        client.close()
    }
}
