package me.vendoor.dragonball.migration.api

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.dsl.CollectionSpecificationListBuilder

class MigrationContext(private val database: MongoDatabase) {
    fun createCollections(lambda: CollectionSpecificationListBuilder.() -> Unit) {

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