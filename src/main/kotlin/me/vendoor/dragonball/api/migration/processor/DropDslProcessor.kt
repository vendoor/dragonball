package me.vendoor.dragonball.api.migration.processor

import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.api.dsl.drop.DropContext
import me.vendoor.dragonball.api.util.database.getCollectionIfExists

class DropContextProcessor(private val database: MongoDatabase,
                           private val context: DropContext) {
    fun process() {
        context.collections.forEach {
            database.getCollectionIfExists(it)
                    ?.drop()
        }

        context.indexes.forEach {
            database.getCollectionIfExists(it.collectionName)
                    ?.dropIndex(it.indexName)
        }
    }
}
