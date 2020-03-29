package me.vendoor.dragonball.api.util.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

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