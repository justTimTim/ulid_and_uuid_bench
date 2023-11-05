### Java jdbc benchmark

This service was written to measure the speed of requests to the Postgres database when using the
types UUID and ULID.


The settings for the database are in the DbResourceUlid and DbResourceUuid classes.

For start
1. mvn clean package
2. go to the target directory
3. java -jar .\bench-0.0.1-SNAPSHOT.jar