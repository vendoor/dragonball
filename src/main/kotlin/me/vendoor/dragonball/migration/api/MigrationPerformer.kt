package me.vendoor.dragonball.migration.api

import com.github.zafarkhaja.semver.Version
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.Document
import java.util.NavigableMap
import java.util.Objects.isNull
import java.util.TreeMap
import kotlin.Comparator

object MigrationPerformer {
    private val scripts: NavigableMap<String, MigrationScript> = TreeMap<String, MigrationScript>(SemverComparator())

    fun registerMigrationScript(script: MigrationScript) {
        scripts[script.getVersion()] = script;
    }

    fun perform(targetVersion: String, client: MongoClient) {
        var database = openDatabase(client)

        if (isNull(database)) {
            // setup
        }

        val currentVersion = retrieveCurrentVersion(database)

        val applicableMap = scripts.subMap(currentVersion, false, targetVersion, true)

        val context = MigrationContext(database)

        applicableMap.forEach { (version, script) ->
            script.perform(context)
        }
    }

    private fun openDatabase(client: MongoClient): MongoDatabase? {
        return null;
    }

    private fun retrieveCurrentVersion(database: MongoDatabase): String {
        val migrationCollection = database.getCollection("Migration")

        return migrationCollection.find(BsonDocument())
                .sort(BsonDocument("timestamp", BsonInt32(-1)))
                .first()
                ?.getString("version")
                ?: "1.0.0"
    }

    private class SemverComparator : Comparator<String> {
        override fun compare(o1: String?, o2: String?): Int {
            val version1 = Version.valueOf(o1)
            val version2 = Version.valueOf(o2)

            return version1.compareTo(version2);
        }
    }
}