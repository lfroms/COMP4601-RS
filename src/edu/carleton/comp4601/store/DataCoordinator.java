package edu.carleton.comp4601.store;

import java.util.Optional;

import edu.carleton.comp4601.models.PageDocument;
import edu.carleton.comp4601.models.UserDocument;
import edu.carleton.comp4601.store.mongo.MongoDBConfig;
import edu.carleton.comp4601.store.mongo.MongoProvider;

public final class DataCoordinator {
	private static DataCoordinator singleInstance = null;

	public static DataCoordinator getInstance() {
		if (singleInstance == null) {
			singleInstance = new DataCoordinator();
		}

		return singleInstance;
	}

	// STORABLE INSTANCES ===============================================================

	private static MongoProvider<PageDocument> pagesDatabase =
			new MongoProvider<>(getPagesDatabaseConfiguration());
	
	private static MongoProvider<UserDocument> usersDatabase =
			new MongoProvider<>(getUsersDatabaseConfiguration());


	// PUBLIC INTERFACE =================================================================

	public void upsert(UserDocument user) {
		usersDatabase.upsert(user);
	}
	
	public void upsert(PageDocument page) {
		pagesDatabase.upsert(page);
	}

	public Optional<PageDocument> findPage(String id) {
		return pagesDatabase.find(id, PageDocument.class);
	}
	
	public Optional<UserDocument> findUser(String id) {
		return usersDatabase.find(id, UserDocument.class);
	}

	public void deletePage(String id) {
		pagesDatabase.delete(id);
	}
	
	public void deleteUser(String id) {
		usersDatabase.delete(id);
	}

	public void reset() {
		pagesDatabase.reset();
		usersDatabase.reset();
	}

	// PROVIDER CONFIGURATION ===========================================================

	private static final MongoDBConfig getPagesDatabaseConfiguration() {
		return new MongoDBConfig("localhost", 27017, "crawler", "pages");
	}
	
	private static final MongoDBConfig getUsersDatabaseConfiguration() {
		return new MongoDBConfig("localhost", 27017, "crawler", "users");
	}
}
