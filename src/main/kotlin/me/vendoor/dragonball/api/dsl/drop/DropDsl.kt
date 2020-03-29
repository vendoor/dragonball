package me.vendoor.dragonball.api.dsl.drop

import me.vendoor.dragonball.api.util.database.IndexSort
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.conversions.Bson

@DslMarker annotation class DropDslMarker

data class DropIndexContext(
        var collectionName: String,
        var fields: Bson
)

@DropDslMarker
class DropIndexContextBuilder {
    private var collectionName = ""
    private var fields  = BsonDocument()

    fun inCollection(lambda: () -> String)  {
        collectionName = lambda()
    }

    fun fields(lambda: FieldCollector.() -> Unit) {
        val fieldMap: MutableMap<String, IndexSort> = HashMap()
        val fieldCollector = FieldCollector(fieldMap)

        lambda(fieldCollector)

        fields = mapToIndexFields(fieldMap)
    }

    fun build() = DropIndexContext(collectionName, fields)

    private fun mapToIndexFields(fields: MutableMap<String, IndexSort> = HashMap()): BsonDocument {
        val result = BsonDocument()

        fields.forEach { (name, sort) ->
            result[name] = BsonInt32(sort.intValue)
        }

        return result
    }

    class FieldCollector(private val fields: MutableMap<String, IndexSort> = HashMap()) {
        fun field(name: String, sort: IndexSort) {
            fields[name] = sort
        }
    }
}

data class DropContext(
        val collections: List<String>,
        val indexes: List<DropIndexContext>
)

@DropDslMarker
class DropContextBuilder {
    private val collections: MutableList<String> = ArrayList()
    private val indexes: MutableList<DropIndexContext> = ArrayList()

    fun collection(lambda: () -> String) {
        collections.add(lambda())
    }

    fun index(lambda: DropIndexContextBuilder.() -> Unit) {
        val builder = DropIndexContextBuilder()

        lambda(builder)

        indexes.add(builder.build())
    }

    fun build() = DropContext(collections, indexes)
}
