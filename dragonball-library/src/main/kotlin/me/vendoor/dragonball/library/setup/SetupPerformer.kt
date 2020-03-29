package me.vendoor.dragonball.library.setup

import com.mongodb.client.MongoClient
import me.vendoor.dragonball.library.configuration.Configuration
import me.vendoor.dragonball.schema.dsl.upsert.CreateDatabaseContext

class SetupPerformer(private val configuration: Configuration, private val client: MongoClient) {
    fun setupDatabaseFromSpecification(context: CreateDatabaseContext) {
        DatabaseSpecificationProcessor(configuration, client, context).process()
    }
}