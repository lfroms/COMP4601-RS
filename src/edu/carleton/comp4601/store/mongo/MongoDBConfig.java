package edu.carleton.comp4601.store.mongo;

public final class MongoDBConfig {
	private final String hostname;
	private final Integer port;
	private final String databaseName;
	private final String collectionName;
	
	public MongoDBConfig(String hostname, Integer port, String databaseName, String collectionName) {
		this.hostname = hostname;
		this.port = port;
		this.databaseName = databaseName;
		this.collectionName = collectionName;
	}
	
	// GETTERS ==========================================================================
	
	public String getHostname() {
		return hostname;
	}
	
	public Integer getPort() {
		return port;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public String getCollectionName() {
		return collectionName;
	}
}
