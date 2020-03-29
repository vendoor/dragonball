package me.vendoor.dragonball.library.configuration

class Configuration {
    // Abusing lateinit? Maybe.
    lateinit var database: Database

    class Database {
        lateinit var connectionString: String
        lateinit var name: String
    }
}