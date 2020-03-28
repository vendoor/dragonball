package me.vendoor.dragonball.dsl

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.BsonDocument
import java.lang.Exception

fun setupDatabaseFromSpecification(client: MongoClient, specification: DatabaseSpecification) =
    DatabaseSpecificationProcessor(client, specification).process()

private class DatabaseSpecificationProcessor(val client: MongoClient, val specification: DatabaseSpecification) {
    fun process() {
        val database = obtainDatabase()

        specification.collections.forEach { collection ->
            CollectionSpecificationProcessor(database, collection).process()
        }
    }

    private fun obtainDatabase() = client.getDatabase(specification.name)
}

private class CollectionSpecificationProcessor(val database: MongoDatabase, val specification: CollectionSpecification) {
    fun process() {
        val collection = obtainCollection()

        specification.indexes.forEach { index ->
            IndexSpecificationProcessor(collection, index).process()
        }
    }

    private fun obtainCollection(): MongoCollection<BsonDocument> {
        if (isCollectionMissing()) {
            database.createCollection(specification.name, specification.options)

            println("Created collection: ${specification.name}")
        }

        return database.getCollection(specification.name, BsonDocument::class.java)
    }

    private fun isCollectionMissing() = !database.listCollectionNames()
            .any { name -> name == specification.name }
}

private class IndexSpecificationProcessor(val collection: MongoCollection<BsonDocument>, val specification: IndexSpecification) {
    fun process() {
        try {
            collection.createIndex(specification.fields, specification.options.name(specification.name))

            println("Created index: ${specification.name}")
        } catch (e: Exception) {
            // No-op, index already exists.
        }
    }
}
