package me.vendoor.dragonball

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import me.vendoor.dragonball.commands.Setup

class Dragonball: CliktCommand(
        help = "Command line database administration toolkit.",
        invokeWithoutSubcommand = false
) {
    override fun run() = Unit
}

fun main(args: Array<String>) = Dragonball()
        .subcommands(Setup())
        .main(args)
