package me.vendoor.dragonball.library.schema.dsl.drop

@DslMarker annotation class DropDslMarker

data class DropIndexContext(
        var collectionName: String,
        var indexName: String
)

@DropDslMarker
class DropIndexContextBuilder {
    private var collectionName = ""
    private var indexName  = ""

    fun inCollection(lambda: () -> String)  {
        collectionName = lambda()
    }

    fun name(lambda: () -> String) {
        indexName = lambda()
    }

    fun build() = DropIndexContext(collectionName, indexName)
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
