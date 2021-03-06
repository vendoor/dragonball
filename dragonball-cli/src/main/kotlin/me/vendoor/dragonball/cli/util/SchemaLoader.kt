package me.vendoor.dragonball.cli.util

import me.vendoor.dragonball.library.schema.Schema
import java.io.File
import java.net.URLClassLoader

class SchemaLoader {
    fun loadSchemaFromJar(file: File, name: String): Schema {
        val jarUrl = file.toURL()

        val classLoader = URLClassLoader(
                arrayOf(jarUrl),
                this.javaClass.classLoader
        )

        @Suppress("UNCHECKED_CAST")
        val clazz = classLoader.loadClass(name) as Class<Schema>

        return clazz.getConstructor().newInstance()
    }
}