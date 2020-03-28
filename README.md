# Dragonball

Command line database administration toolkit.

## Building Dragonball

Assuming, you have a JDK installed, Dragonball can be built using maven:

~~~~
./mvnw clean package
~~~~

This creates the following JARs:

  * `target/dragonball-{version}.jar`
  * `target/dragonball-latest.jar`

## Configuration

Dragonball commands make use of a configuration file (in HOCON format). An example configuration file is provided at [config/vendoor.conf](config/vendoor.conf). This file holds shared settings such as database connection strings, database names and such.

## Using Dragonball

### Setup

Initializes an empty database.

The database specification (collections, indexes, etc.) can be found in [VendoorDatabaseSpecification.kt](src/main/kotlin/me/vendoor/dragonball/specification/VendoorDatabaseSpecification.kt). Modify this file if you want to configure the way the database is initialized (for example, a new collection should be created). For a primer on the specification DSL, please refer to [Specification DSL](docs/specification-dsl.md).

~~~~
java -jar target/dragonball-latest.jar setup --config-file=./config/vendoor.conf
~~~~

### Migrate

Performs a database migration between the specified versions.

This command is not implemented yet :(

## Notes on Specification, Migration and Versioning

If you create a new migration script, you should also take care of the following:

  * modify accordingly the database specification used by the `setup` command,
  * release a new version of this application, describing the changes in the release notes.
