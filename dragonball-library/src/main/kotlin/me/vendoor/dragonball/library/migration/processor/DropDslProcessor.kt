package me.vendoor.dragonball.library.migration.processor

import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.common.database.getCollectionIfExists
import me.vendoor.dragonball.library.schema.dsl.drop.DropContext

class DropContextProcessor(private val database: MongoDatabase,
                           private val context: DropContext) {
    fun process() {
        context.collections.forEach {
            database.getCollectionIfExists(it)
                    ?.run {
                        this.drop()
                    }
        }

        context.indexes.forEach {
            database.getCollectionIfExists(it.collectionName)
                    ?.dropIndex(it.indexName)
        }
    }
}
