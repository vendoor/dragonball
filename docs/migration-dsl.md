# Migration DSL

A declarative Kotlin DSL which can be used to change or drop existing collections and create new collections or indexes.

It can be best understood using an example:

~~~~Kotlin
class ExampleMigration : MigrationScript(
        version = "1.1.0",
        description = "An example migration"
) {
    override fun migrate(context: MigrationContext) {
        context.drop {
            collection { "Dropme" }

            index {
                inCollection { "Collection" }
                name { "Dropme" }
            }
        }

        val someCollection = context.collection("ModifyTheDocs")
        someCollection.updateMany(/* update */)

        context.create {
            collection {
                // The same as in the Specification DSL.
                name { "Customers" }
            }
        }
    }
}
~~~~

That's all it supports for now :)
