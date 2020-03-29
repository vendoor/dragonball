package me.vendoor.dragonball.library.schema

import me.vendoor.dragonball.library.schema.migration.MigrationScript
import me.vendoor.dragonball.library.schema.specification.Specification

interface Schema {
    fun getSpecification(): Specification

    fun getMigrationScripts(): List<MigrationScript>
}