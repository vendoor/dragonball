package me.vendoor.dragonball.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.vendoor.dragonball.cli.commands.Migrate
import me.vendoor.dragonball.cli.commands.Setup

class Dragonball: CliktCommand(
        help = "Command line database administration toolkit.",
        invokeWithoutSubcommand = false
) {
    override fun run() = Unit
}

fun main(args: Array<String>) = Dragonball()
        .subcommands(Migrate(), Setup())
        .main(args)
