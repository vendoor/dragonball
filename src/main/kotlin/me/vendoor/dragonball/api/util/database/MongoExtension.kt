package me.vendoor.dragonball.api.util.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.api.dsl.IndexSort

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

fun MongoDatabase.getCollectionIfExists(name: String) =
        if (this.hasCollection(name)) {
            this.getCollection(name)
        } else {
            null
        }

fun MongoCollection<*>.hasIndexOnFields(fields: Map<String, IndexSort>): Nothing =
        TODO()