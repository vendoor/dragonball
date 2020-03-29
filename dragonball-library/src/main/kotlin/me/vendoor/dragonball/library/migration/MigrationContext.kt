package me.vendoor.dragonball.library.migration

import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.library.migration.processor.CreateCollectionListContextProcessor
import me.vendoor.dragonball.library.migration.processor.DropContextProcessor
import me.vendoor.dragonball.schema.dsl.drop.DropContextBuilder
import me.vendoor.dragonball.schema.dsl.upsert.CreateCollectionListContextBuilder

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