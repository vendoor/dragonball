# Specification DSL

A declarative Kotlin DSL which can be used to specify the desired database structure. Currently, it only supports MongoDB.

It can be best understood using an example:

~~~~Kotlin
database {
    name { config.getString("database.name") }

    collections {
        collection {
            name { "Users" }
        }

        collection {
            name { "CappedCollection" }

            options {
                // You have access to the fields of the CreateCollectionOptions class.
                capped(true)
                sizeInBytes(1024)
            }
        }

        collection {
            name { "CollectionWithAnIndex" }

            indexes {
                index {
                    name { "SomeIndex" },

                    // Will be converted to BSON.
                    fields { """
                        {
                            "timestamp": 1
                        }
                    """.trimIndent() }
                    
                    options {
                        // You have access to the fields of the IndexOptions class.
                        unique(true)
                    }
                }
            }
        }
    }
}
~~~~

That's all it supports for now :)
