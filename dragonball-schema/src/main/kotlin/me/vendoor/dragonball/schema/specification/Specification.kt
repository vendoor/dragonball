package me.vendoor.dragonball.schema.specification

import me.vendoor.dragonball.schema.dsl.upsert.CreateDatabaseContext

interface Specification {
    fun get(): CreateDatabaseContext
}