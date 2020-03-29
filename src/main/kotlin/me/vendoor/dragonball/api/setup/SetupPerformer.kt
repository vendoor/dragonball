package me.vendoor.dragonball.api.setup

import com.mongodb.client.MongoClient
import me.vendoor.dragonball.api.configuration.Configuration
import me.vendoor.dragonball.api.dsl.upsert.CreateDatabaseContext

class SetupPerformer(private val configuration: Configuration, private val client: MongoClient) {
    fun setupDatabaseFromSpecification(context: CreateDatabaseContext) {
        DatabaseSpecificationProcessor(configuration, client, context).process()
    }
}