package me.vendoor.dragonball.api.migration.processor

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.api.dsl.upsert.CreateCollectionContext
import me.vendoor.dragonball.api.dsl.upsert.CreateIndexContext
import org.bson.Document

class CreateCollectionListContextProcessor(private val database: MongoDatabase,
                                           private val collections: List<CreateCollectionContext>) {
    fun process() {
        collections.forEach {
            CollectionSpecificationProcessor(database, it).process()
        }
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