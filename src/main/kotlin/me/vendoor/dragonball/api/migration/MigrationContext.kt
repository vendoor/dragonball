package me.vendoor.dragonball.api.migration

import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.api.dsl.upsert.CreateCollectionSpecificationContextBuilder

class MigrationContext(private val database: MongoDatabase) {
    fun createCollections(lambda: CreateCollectionSpecificationContextBuilder.() -> Unit) {

    }

    fun removeCollections(names: List<String>) =
            names.forEach { name ->
                database.getCollection(name).drop()
            }

    fun collection(name: String) =
            database.getCollection(name)

    fun <TDocument> collection(name: String, documentClass: Class<TDocument>) =
            database.getCollection(name, documentClass)
}