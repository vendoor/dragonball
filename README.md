# Dragonball

Command line database administration toolkit.

## Building Dragonball

Assuming, you have a JDK installed, Dragonball can be built using maven:

~~~~
./mvnw clean package
~~~~

## Configuration

Dragonball commands make use of a configuration file (in HOCON format). An example configuration file is provided at [config/vendoor.conf](config/vendoor.conf). This file holds shared settings such as database connection strings, database names and such.

## Using Dragonball

### Setup

Initializes an empty database.

For a primer on the specification DSL, please refer to [Specification DSL](docs/specification-dsl.md).

~~~~
java -jar .\dragonball-cli\target\dragonball-cli-1.0.0.jar setup \
  --config-file=./config/vendoor.conf \
  --schema-jar=../schema/target/schema-1.0.0.jar \
  --schema-class="me.vendoor.schema.Schema" --target-version="1.1.0"
~~~~

### Migrate

Performs a database migration between the specified versions.

For a primer on the migration DSL, please refer to [Migration DSL](docs/migration-dsl.md).

~~~~
java -jar .\dragonball-cli\target\dragonball-cli-1.0.0.jar migrate \
  --config-file=./config/vendoor.conf \
  --target-version="1.1.0" \
  --schema-jar=../schema/target/schema-1.0.0.jar \
  --schema-class="me.vendoor.schema.Schema"
~~~~

## Notes on Specification, Migration and Versioning

If you create a new migration script, you should also take care of the following:

  * modify accordingly the database specification used by the `setup` command,
  * release a new version of this application, describing the changes in the release notes.
