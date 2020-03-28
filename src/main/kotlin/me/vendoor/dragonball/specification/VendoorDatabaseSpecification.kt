package me.vendoor.dragonball.specification

import com.typesafe.config.Config
import me.vendoor.dragonball.dsl.database

fun vendoorDatabaseSpecification(config: Config) = database {
    name { config.getString("database.name") }

    collections {
    }
}
