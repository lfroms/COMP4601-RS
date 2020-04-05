# Contextual Advertising System
* Lukas Romsicki (101059080)
* Britta Evans-Fenton (101017131)

## Prerequisites
* Java 13
* MongoDB 4.2.2

## Preliminary
The following steps must be followed in order to ensure that the app is in a runnable state.
1. Start a MongoDB server at `localhost` with port `27017`.
2. Deploy the WAR file.
3. (if needed) You may alternatively run the software through Eclipse:
   1. Right-click on the project root, then click `Gradle > Refresh Gradle Project`.  You must have a working internet connection for this to work.
      * This step will download all the necessary dependencies to your machine.
   4. Go to `Project > Clean` to clean the project of any possible artifacts.

## Performing a crawl

1. Navigate to [http://localhost:8080/COMP4601-RS/rest/rs/crawl](http://localhost:8080/COMP4601-RS/rest/rs/crawl).
   * This will load the database with data and perform initial analysis.
2. You may then access any of the specified web services as outlined in the assignment specification.
