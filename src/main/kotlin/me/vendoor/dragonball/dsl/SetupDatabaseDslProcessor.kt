package me.vendoor.dragonball.dsl

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.CreateIndexOptions
import com.mongodb.client.model.IndexOptions
import me.vendoor.dragonball.util.time.TimeSource
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.BsonInt64
import org.bson.BsonString
import org.bson.conversions.Bson
import java.lang.Exception

fun setupDatabaseFromSpecification(client: MongoClient, specification: DatabaseSpecification) =
    DatabaseSpecificationProcessor(client, specification).process()

private class DatabaseSpecificationProcessor(val client: MongoClient, val specification: DatabaseSpecification) {
    companion object {
        private const val MIGRATION_COLLECTION_NAME = "Migration"
        private const val MIGRATION_COLLECTION_TIMESTAMP_INDEX_NAME = "migration-timestamp-desc"
        private const val SETUP_MIGRATION_DESCRIPTION = "New setup."
    }

    fun process() {
        val database = obtainDatabase()

        specification.collections.forEach { collection ->
            CollectionSpecificationProcessor(database, collection).process()
        }

        recordMigration(specification.version, database)
    }

    private fun obtainDatabase() = client.getDatabase(specification.name)

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

private class CollectionSpecificationProcessor(val database: MongoDatabase, val specification: CollectionSpecification) {
    fun process() {
        val collection = obtainCollection(specification.name, specification.options, database);

        specification.indexes.forEach { index ->
            IndexSpecificationProcessor(collection, index).process()
        }
    }
}

private class IndexSpecificationProcessor(val collection: MongoCollection<BsonDocument>, val specification: IndexSpecification) {
    fun process() {
        createIndex(specification.fields, specification.name, specification.options, collection);
        try {
            collection.createIndex(specification.fields, specification.options.name(specification.name))

            println("Created index: ${specification.name}")
        } catch (e: Exception) {
            // No-op, index already exists.
        }
    }
}

private fun createIndex(fields: Bson, name: String, options: IndexOptions, collection: MongoCollection<*>) {
    try {
        collection.createIndex(fields, options.name(name));

        println("Created index: ${name}")
    } catch (e: Exception) {
        // No-op, index already exists.
    }
}

private fun obtainCollection(name: String, options: CreateCollectionOptions, database: MongoDatabase): MongoCollection<BsonDocument> {
    if (isCollectionMissing(name, database)) {
        database.createCollection(name, options)

        println("Created collection: ${name}")
    }

    return database.getCollection(name, BsonDocument::class.java)
}

private fun isCollectionMissing(name: String, database: MongoDatabase) = !database.listCollectionNames()
        .any { actualName -> actualName == name }