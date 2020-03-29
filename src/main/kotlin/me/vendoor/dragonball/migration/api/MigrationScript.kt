package me.vendoor.dragonball.migration.api

import com.mongodb.client.MongoDatabase

interface MigrationScript {
    fun getVersion(): String

    fun getDescription(): String

    fun perform(context: MigrationContext)
}