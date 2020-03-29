package me.vendoor.dragonball.migration.api

import com.github.zafarkhaja.semver.Version
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.typesafe.config.Config
import me.vendoor.dragonball.util.database.getCollectionIfExists
import me.vendoor.dragonball.util.database.getDatabaseIfExists
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.BsonString
import java.util.NavigableMap
import java.util.TreeMap
import kotlin.Comparator

object MigrationPerformer {
    private val scripts: NavigableMap<String, MigrationScript> = TreeMap<String, MigrationScript>(SemverComparator())

    fun registerMigrationScript(script: MigrationScript) {
        scripts[script.getVersion()] = script;
    }

    fun perform(config: Config, targetVersion: String, client: MongoClient) {
        val database = client.getDatabaseIfExists(config.getString("database.name"))

        if (database == null) {
            println("Database does not exist, please first create it using setup!")
            return
        }

        val currentVersion = retrieveCurrentVersion(database)

        if (currentVersion == null) {
            println("Could not read the actual database version!")
            return
        }

        val applicableMap = scripts.subMap(currentVersion, false, targetVersion, true)

        val context = MigrationContext(database)

        applicableMap.forEach { (version, script) ->
            script.perform(context)
        }
    }

    private fun retrieveCurrentVersion(database: MongoDatabase): String? {
        val migrationCollection = database.getCollectionIfExists("Migration")
                ?: return null

        return migrationCollection.find(BsonDocument())
                .sort(BsonDocument("timestamp", BsonInt32(-1)))
                .first()
                ?.get("version")
                ?.let {
                    if (it is BsonString) {
                        return it.value
                    } else {
                        return null
                    }
                }
    }

    private class SemverComparator : Comparator<String> {
        override fun compare(o1: String?, o2: String?): Int {
            val version1 = Version.valueOf(o1)
            val version2 = Version.valueOf(o2)

            return version1.compareTo(version2);
        }
    }
}