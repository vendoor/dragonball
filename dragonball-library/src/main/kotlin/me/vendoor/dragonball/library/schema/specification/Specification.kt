package me.vendoor.dragonball.library.schema.specification

import me.vendoor.dragonball.library.schema.dsl.upsert.CreateDatabaseContext

interface Specification {
    fun get(): CreateDatabaseContext
}