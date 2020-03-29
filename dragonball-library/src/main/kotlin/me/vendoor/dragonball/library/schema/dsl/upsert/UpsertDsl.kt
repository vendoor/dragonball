package me.vendoor.dragonball.library.schema.dsl.upsert

import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.IndexOptions
import me.vendoor.dragonball.common.database.IndexSort
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.conversions.Bson

@DslMarker annotation class UpsertDslMarker

data class CreateIndexContext(
        var name: String,
        var fields: Bson,
        var options: IndexOptions
)

@UpsertDslMarker
class CreateIndexContextBuilder {
    private var name = ""
    private var fields  = BsonDocument()
    private var options = IndexOptions()

    fun name(lambda: () -> String)  {
        name = lambda()
    }

    fun fields(lambda: FieldCollector.() -> Unit) {
        val fieldMap: MutableMap<String, IndexSort> = HashMap()
        val fieldCollector = FieldCollector(fieldMap)

        lambda(fieldCollector)

        fields = mapToIndexFields(fieldMap)
    }

    fun options(lambda: IndexOptions.() -> Unit) {
        options = options.apply(lambda)
    }

    fun build() = CreateIndexContext(name, fields, options)

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

@UpsertDslMarker
class CreateIndexContextListBuilder {
    private var indexes = ArrayList<CreateIndexContext>()

    fun index(lambda: CreateIndexContextBuilder.() -> Unit) {
        val builder = CreateIndexContextBuilder()

        indexes.add(builder.apply(lambda).build())
    }

    fun build() = indexes
}

data class CreateCollectionContext(
        var name: String,
        var indexes: List<CreateIndexContext>,
        var options: CreateCollectionOptions
)

@UpsertDslMarker
class CreateCollectionContextBuilder {
    private var name = ""
    private var indexes = ArrayList<CreateIndexContext>()
    private var options = CreateCollectionOptions()

    fun name(lambda: () -> String)  {
        name = lambda()
    }

    fun indexes(lambda: CreateIndexContextListBuilder.() -> Unit) {
        val builder = CreateIndexContextListBuilder()

        indexes = builder.apply(lambda).build()
    }

    fun options(lambda: CreateCollectionOptions.() -> Unit) {
        options = options.apply(lambda)
    }

    fun build() = CreateCollectionContext(name, indexes, options)
}

@UpsertDslMarker
class CreateCollectionListContextBuilder {
    private var collections = ArrayList<CreateCollectionContext>()

    fun collection(lambda: CreateCollectionContextBuilder.() -> Unit) {
        val builder = CreateCollectionContextBuilder()

        collections.add(builder.apply(lambda).build())
    }

    fun build() = collections
}

data class CreateDatabaseContext(
        var version: String,
        var collections: List<CreateCollectionContext>
)

@UpsertDslMarker
class CreateDatabaseContextBuilder {
    private var version = ""
    private var collections = ArrayList<CreateCollectionContext>()

    fun version(lambda: () -> String) {
        version = lambda()
    }

    fun collections(lambda: CreateCollectionListContextBuilder.() -> Unit) {
        val builder = CreateCollectionListContextBuilder()

        collections = builder.apply(lambda).build()
    }

    fun build() = CreateDatabaseContext(version, collections)
}

fun database(lambda: CreateDatabaseContextBuilder.() -> Unit): CreateDatabaseContext {
    val builder = CreateDatabaseContextBuilder()

    return builder.apply(lambda).build()
}
