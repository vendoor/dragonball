package me.vendoor.dragonball.specification

import me.vendoor.dragonball.api.dsl.database

fun vendoorDatabaseSpecification() = database {
    name { "vendoor" }

    version { "1.0.0" }

    collections {
    }
}
