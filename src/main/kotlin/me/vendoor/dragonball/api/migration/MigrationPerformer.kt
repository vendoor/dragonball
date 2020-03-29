package me.vendoor.dragonball.api.migration

import com.github.zafarkhaja.semver.Version
import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.api.configuration.Configuration
import me.vendoor.dragonball.api.util.database.getCollectionIfExists
import org.bson.BsonDocument
import org.bson.BsonInt32
import java.util.NavigableMap
import java.util.TreeMap
import kotlin.Comparator

class MigrationPerformer(
        private val configuration: Configuration,
        private val database: MongoDatabase
) {
    fun perform(targetVersion: String, scripts: List<MigrationScript>) {
        val currentVersion = retrieveCurrentVersion(database)

        if (currentVersion == null) {
            println("Could not read the actual database version!")

            return
        }

        val navigableScripts = scriptListToNavigableMap(scripts)
        val applicableScripts = navigableScripts.subMap(currentVersion, false, targetVersion, true)

        val context = MigrationContext(database)

        applicableScripts.forEach { (version, script) ->
            script.perform(context)
        }
    }

    private fun retrieveCurrentVersion(database: MongoDatabase): String? {
        val migrationCollection = database.getCollectionIfExists("Migration", StoredMigration::class.java)
                ?: return null

        return migrationCollection.find(BsonDocument())
                .sort(BsonDocument("timestamp", BsonInt32(-1)))
                .first()
                ?.version
    }

    private fun scriptListToNavigableMap(scripts: List<MigrationScript>): NavigableMap<String, MigrationScript> {
        val result = TreeMap<String, MigrationScript>(SemverComparator())

        scripts.forEach {
            result[it.getVersion()] = it
        }

        return result
    }

    private class SemverComparator : Comparator<String> {
        override fun compare(o1: String?, o2: String?): Int {
            val version1 = Version.valueOf(o1)
            val version2 = Version.valueOf(o2)

            return version1.compareTo(version2);
        }
    }
}