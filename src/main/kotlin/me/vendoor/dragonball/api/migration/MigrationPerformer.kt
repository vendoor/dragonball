package me.vendoor.dragonball.api.migration

import com.github.zafarkhaja.semver.Version
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import me.vendoor.dragonball.api.util.database.IndexSort
import me.vendoor.dragonball.api.util.database.getCollectionIfExists
import me.vendoor.dragonball.api.util.time.TimeSource
import org.bson.BsonDocument
import org.bson.BsonInt32
import java.util.NavigableMap
import java.util.TreeMap
import kotlin.Comparator

class MigrationPerformer(
        private val database: MongoDatabase
) {
    companion object {
        private const val COLLECTION_NAME = "Migration"
        private const val INITIAL_DESCRIPTION = "New setup."
    }

    fun setupMigration(initialVersion: String) {
        val collection = createMigrationCollection()

        collection.insertOne(initialMigration(initialVersion))
    }

    fun perform(targetVersion: String) {
        val currentVersion = retrieveCurrentVersion(database)

        if (currentVersion == null) {
            println("Could not read the actual database version!")

            return
        }

        val navigableScripts = scriptListToNavigableMap(MigrationScriptRegistry.getRegisteredScripts())
        val applicableScripts = navigableScripts.subMap(currentVersion, false, targetVersion, true)

        val context = MigrationContext(database)

        applicableScripts.forEach { (version, script) ->
            println("Performing migration to $version")

            script.migrate(context)
        }
    }

    private fun createMigrationCollection(): MongoCollection<StoredMigration> {
        database.createCollection(COLLECTION_NAME)

        val collection = database.getCollection(COLLECTION_NAME, StoredMigration::class.java)

        collection.createIndex(BsonDocument("timestamp", BsonInt32(IndexSort.DESCENDING.intValue)))

        return collection
    }

    private fun initialMigration(initialVersion: String): StoredMigration {
        val setup = StoredMigration()
        setup.timestamp = TimeSource.currentTimestamp()
        setup.version = initialVersion
        setup.description = INITIAL_DESCRIPTION
        return setup
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

            return version1.compareTo(version2)
        }
    }
}