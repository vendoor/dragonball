package me.vendoor.dragonball.api.setup

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.IndexOptions
import me.vendoor.dragonball.api.dsl.CreateCollectionContext
import me.vendoor.dragonball.api.dsl.CreateDatabaseContext
import me.vendoor.dragonball.api.dsl.CreateIndexContext
import me.vendoor.dragonball.api.util.database.hasCollection
import me.vendoor.dragonball.api.util.time.TimeSource
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.BsonInt64
import org.bson.BsonString
import org.bson.conversions.Bson
import java.lang.Exception

fun setupDatabaseFromSpecification(client: MongoClient, context: CreateDatabaseContext) =
    DatabaseSpecificationProcessor(client, context).process()

private class DatabaseSpecificationProcessor(val client: MongoClient, val context: CreateDatabaseContext) {
    companion object {
        private const val MIGRATION_COLLECTION_NAME = "Migration"
        private const val MIGRATION_COLLECTION_TIMESTAMP_INDEX_NAME = "migration-timestamp-desc"
        private const val SETUP_MIGRATION_DESCRIPTION = "New setup."
    }

    fun process() {
        val database = obtainDatabase()

        context.collections.forEach { collection ->
            CollectionSpecificationProcessor(database, collection).process()
        }

        recordMigration(context.version, database)
    }

    private fun obtainDatabase() = client.getDatabase(context.name)

    private fun recordMigration(version: String, database: MongoDatabase) {
        val options = CreateCollectionOptions()

        val collection = obtainCollection(MIGRATION_COLLECTION_NAME, options, database)

        createMigrationIndex(collection)

        collection.insertOne(versionDocument(version))
    }

    private fun createMigrationIndex(collection: MongoCollection<*>) {
        val fields = BsonDocument("timestamp", BsonInt32(-1))
        val options = IndexOptions()

        createIndex(fields, MIGRATION_COLLECTION_TIMESTAMP_INDEX_NAME, options, collection)
    }

    private fun versionDocument(version: String): BsonDocument {
        val document = BsonDocument();

        document["timestamp"] = BsonInt64(TimeSource.currentTimestamp())
        document["version"] = BsonString(version)
        document["description"] = BsonString(SETUP_MIGRATION_DESCRIPTION)

        return document
    }
}

private class CollectionSpecificationProcessor(val database: MongoDatabase, val context: CreateCollectionContext) {
    fun process() {
        val collection = obtainCollection(context.name, context.options, database);

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
        collection.createIndex(fields, options.name(name));

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
