package me.vendoor.dragonball.specification

import me.vendoor.dragonball.api.dsl.upsert.database

fun vendoorDatabaseSpecification() = database {
    name { "vendoor" }

    version { "1.0.0" }

    collections {
    }
}
