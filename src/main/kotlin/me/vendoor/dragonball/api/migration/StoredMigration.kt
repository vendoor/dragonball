package me.vendoor.dragonball.api.migration

import org.bson.types.ObjectId

class StoredMigration {
    var id: ObjectId? = null
    var version: String? = null
    var timestamp: Long = 0
    var description: String? = null
}