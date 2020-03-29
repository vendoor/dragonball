package me.vendoor.dragonball.schema

import me.vendoor.dragonball.schema.migration.MigrationScript
import me.vendoor.dragonball.schema.specification.Specification

interface Schema {
    fun getSpecification(): Specification

    fun getMigrationScripts(): List<MigrationScript>
}