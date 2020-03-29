package me.vendoor.dragonball.api.util.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider

fun openClient(connectionString: String): MongoClient {
    val pojoCodecRegistry: CodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()))

    val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(connectionString))
            .codecRegistry(pojoCodecRegistry)
            .build()

    return MongoClients.create(settings)
}

fun MongoClient.hasDatabase(name: String) =
        this.listDatabaseNames().contains(name)

fun MongoClient.getDatabaseIfExists(name: String) =
        if (this.hasDatabase(name)) {
            this.getDatabase(name)
        } else {
            null
        }

fun MongoDatabase.hasCollection(name: String) =
        this.listCollectionNames().contains(name)

fun <TDocument> MongoDatabase.getCollectionIfExists(name: String, documentClass: Class<TDocument>) =
        if (this.hasCollection(name)) {
            this.getCollection(name, documentClass)
        } else {
            null
        }

fun MongoDatabase.getCollectionIfExists(name: String) =
        this.getCollectionIfExists(name, Document::class.java)

fun MongoCollection<*>.hasIndexOnFields(fields: Map<String, IndexSort>): Nothing =
        TODO()