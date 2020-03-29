package me.vendoor.dragonball.api.setup

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.IndexOptions
import me.vendoor.dragonball.api.configuration.Configuration
import me.vendoor.dragonball.api.dsl.creation.CreateCollectionContext
import me.vendoor.dragonball.api.dsl.creation.CreateDatabaseContext
import me.vendoor.dragonball.api.dsl.creation.CreateIndexContext
import me.vendoor.dragonball.api.migration.MigrationPerformer
import me.vendoor.dragonball.api.util.database.hasCollection
import org.bson.BsonDocument
import org.bson.conversions.Bson
import java.lang.Exception

fun setupDatabaseFromSpecification(configuration: Configuration, client: MongoClient, context: CreateDatabaseContext) =
    DatabaseSpecificationProcessor(configuration, client, context).process()

private class DatabaseSpecificationProcessor(val configuration: Configuration, val client: MongoClient,
                                             val context: CreateDatabaseContext) {
    fun process() {
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

private class CollectionSpecificationProcessor(val database: MongoDatabase, val context: CreateCollectionContext) {
    fun process() {
        val collection = obtainCollection(context.name, context.options, database)

        context.indexes.forEach { index ->
            IndexSpecificationProcessor(collection, index).process()
        }
    }
}

private class IndexSpecificationProcessor(val collection: MongoCollection<BsonDocument>, val context: CreateIndexContext) {
    fun process() {
        createIndex(context.fields, context.name, context.options, collection)
    }
}

private fun createIndex(fields: Bson, name: String, options: IndexOptions, collection: MongoCollection<*>) {
    try {
        // TODO: Use collection.hasIndexOnFields()
        collection.createIndex(fields, options.name(name))

        println("Created index: ${name}")
    } catch (e: Exception) {
        // No-op, index already exists.
    }
}

private fun obtainCollection(name: String, options: CreateCollectionOptions, database: MongoDatabase): MongoCollection<BsonDocument> {
    if (!database.hasCollection(name)) {
        database.createCollection(name, options)

        println("Created collection: ${name}")
    }

    return database.getCollection(name, BsonDocument::class.java)
}
