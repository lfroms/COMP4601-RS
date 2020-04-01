# Contextual Advertising System
* Lukas Romsicki (101059080)
* Britta Evans-Fenton (101017131)

## Prerequisites
* Java 13
* MongoDB 4.2.2

## Preliminary
The database, graph, and Lucene index must be loaded with data first for the app to function correctly.
1. Start a MongoDB server at `localhost` with port `27017`.
2. Open the project in Eclipse.
3. Right-click on the project root, then click `Gradle > Refresh Gradle Project`.  You must have a working internet connection for this to work.
    * This step will download all the necessary dependencies to your machine.
4. Go to `Project > Clean` to clean the project of any possible artifacts.

## Performing a crawl

1. Right-click on `CrawlerController.java` in the Project Explorer, then click `Run as > Java Application`. 
2. Wait until the crawl completes.  A completion message will be printed to the Eclipse console.

## Starting the server

You can either:
a) Deploy the _war_ file, or;
b) Launch the server through Eclipse.

Through Eclipse:

_Assuming you still have the Eclipse project open as per the **Preliminary** section._
1. Open a server resource file, for example, `ContextualAdvertisingSystem.java`.
2. Click the green "play" button in the Eclipse toolbar.
3. Wait for the server to launch.
