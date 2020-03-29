package me.vendoor.dragonball.migration.api

import com.github.zafarkhaja.semver.Version
import com.mongodb.client.MongoDatabase
import java.util.NavigableMap
import java.util.TreeMap
import kotlin.Comparator

object MigrationPerformer {
    private val scripts: NavigableMap<String, MigrationScript> = TreeMap<String, MigrationScript>(SemverComparator())

    fun registerMigrationScript(script: MigrationScript) {
        scripts[script.getVersion()] = script;
    }

    fun perform(targetVersion: String, database: MongoDatabase) {
        val currentVersion = retrieveCurrentVersion(database)

        val applicableMap = scripts.subMap(currentVersion, false, targetVersion, true)

        val context = MigrationContext(database)

        applicableMap.forEach { (version, script) ->
            script.perform(context)
        }
    }

    private fun retrieveCurrentVersion(database: MongoDatabase): String {
        return ""
    }

    private class SemverComparator : Comparator<String> {
        override fun compare(o1: String?, o2: String?): Int {
            val version1 = Version.valueOf(o1)
            val version2 = Version.valueOf(o2)

            return version1.compareTo(version2);
        }
    }
}