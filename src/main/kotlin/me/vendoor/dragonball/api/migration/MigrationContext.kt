package me.vendoor.dragonball.api.migration

import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.api.dsl.drop.DropContextBuilder
import me.vendoor.dragonball.api.dsl.upsert.CreateCollectionListContextBuilder
import me.vendoor.dragonball.api.migration.processor.CreateCollectionListContextProcessor
import me.vendoor.dragonball.api.migration.processor.DropContextProcessor

class MigrationContext(private val database: MongoDatabase) {
    fun create(lambda: CreateCollectionListContextBuilder.() -> Unit) {
        val builder = CreateCollectionListContextBuilder()

        lambda(builder)

        val context = builder.build()

        CreateCollectionListContextProcessor(database, context).process()
    }

    fun drop(lambda: DropContextBuilder.() -> Unit) {
        val builder = DropContextBuilder()

        lambda(builder)

        val context = builder.build()

        DropContextProcessor(database, context).process()
    }

    fun collection(name: String) =
            database.getCollection(name)

    fun <TDocument> collection(name: String, documentClass: Class<TDocument>) =
            database.getCollection(name, documentClass)
}