package me.vendoor.dragonball.api.setup

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.api.configuration.Configuration
import me.vendoor.dragonball.api.dsl.upsert.CreateCollectionContext
import me.vendoor.dragonball.api.dsl.upsert.CreateDatabaseContext
import me.vendoor.dragonball.api.dsl.upsert.CreateIndexContext
import me.vendoor.dragonball.api.migration.MigrationPerformer
import me.vendoor.dragonball.api.util.database.hasDatabase
import org.bson.Document

class DatabaseSpecificationProcessor(private val configuration: Configuration,
                                     private val client: MongoClient,
                                     private val context: CreateDatabaseContext) {
    fun process() {
        if (client.hasDatabase(configuration.database.name)) {
            println("The database already exists! Please drop it first!")
            return
        }

        val database = client.getDatabase(configuration.database.name)

        context.collections.forEach { collection ->
            CollectionSpecificationProcessor(database, collection).process()
        }

        recordMigration(context.version, database)
    }

    private fun recordMigration(version: String, database: MongoDatabase) {
        val migrationPerformer = MigrationPerformer(database)

        migrationPerformer.setupMigration(version)
    }
}

private class CollectionSpecificationProcessor(private val database: MongoDatabase,
                                               private val context: CreateCollectionContext) {
    fun process() {
        database.createCollection(context.name, context.options)
        val collection = database.getCollection(context.name)

        context.indexes.forEach { index ->
            IndexSpecificationProcessor(collection, index).process()
        }
    }
}

private class IndexSpecificationProcessor(private val collection: MongoCollection<Document>,
                                          private val context: CreateIndexContext) {
    fun process() {
        collection.createIndex(context.fields, context.options.name(context.name))
    }
}
