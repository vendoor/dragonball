package me.vendoor.dragonball.specification

import com.typesafe.config.Config
import me.vendoor.dragonball.api.dsl.database

fun vendoorDatabaseSpecification(config: Config) = database {
    name { config.getString("database.name") }

    version { "1.0.0" }

    collections {
    }
}
