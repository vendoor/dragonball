package me.vendoor.dragonball.dsl

import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.IndexOptions
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.conversions.Bson

enum class IndexSort(val intValue: Int) {
    ASCENDING(1),
    DESCENDING(-1)
}

@DslMarker annotation class DatabaseDslMarker

data class IndexSpecification(
        var name: String,
        var fields: Bson,
        var options: IndexOptions
)

@DatabaseDslMarker class IndexSpecificationBuilder {
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

    fun build() = IndexSpecification(name, fields, options)

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

@DatabaseDslMarker class IndexSpecificationListBuilder {
    private var indexes = ArrayList<IndexSpecification>()

    fun index(lambda: IndexSpecificationBuilder.() -> Unit) {
        val builder = IndexSpecificationBuilder()

        indexes.add(builder.apply(lambda).build())
    }

    fun build() = indexes
}

data class CollectionSpecification(
        var name: String,
        var indexes: List<IndexSpecification>,
        var options: CreateCollectionOptions
)

@DatabaseDslMarker class CollectionSpecificationBuilder {
    private var name = ""
    private var indexes = ArrayList<IndexSpecification>()
    private var options = CreateCollectionOptions()

    fun name(lambda: () -> String)  {
        name = lambda()
    }

    fun indexes(lambda: IndexSpecificationListBuilder.() -> Unit) {
        val builder = IndexSpecificationListBuilder()

        indexes = builder.apply(lambda).build()
    }

    fun options(lambda: CreateCollectionOptions.() -> Unit) {
        options = options.apply(lambda)
    }

    fun build() = CollectionSpecification(name, indexes, options)
}

@DatabaseDslMarker class CollectionSpecificationListBuilder {
    private var collections = ArrayList<CollectionSpecification>()

    fun collection(lambda: CollectionSpecificationBuilder.() -> Unit) {
        val builder = CollectionSpecificationBuilder()

        collections.add(builder.apply(lambda).build())
    }

    fun build() = collections
}

data class DatabaseSpecification(
        var name: String,
        var version: String,
        var collections: List<CollectionSpecification>
)

@DatabaseDslMarker class DatabaseSpecificationBuilder {
    private var name = ""
    private var version = ""
    private var collections = ArrayList<CollectionSpecification>()

    fun name(lambda: () -> String)  {
        name = lambda()
    }

    fun version(lambda: () -> String) {
        version = lambda()
    }

    fun collections(lambda: CollectionSpecificationListBuilder.() -> Unit) {
        val builder = CollectionSpecificationListBuilder()

        collections = builder.apply(lambda).build()
    }

    fun build() = DatabaseSpecification(name, version, collections);
}

fun database(lambda: DatabaseSpecificationBuilder.() -> Unit): DatabaseSpecification {
    val builder = DatabaseSpecificationBuilder()

    return builder.apply(lambda).build()
}
