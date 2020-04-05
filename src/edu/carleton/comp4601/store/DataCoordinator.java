package edu.carleton.comp4601.store;

import java.util.List;
import java.util.Optional;

import edu.carleton.comp4601.analyzers.GenrePreprocessor;
import edu.carleton.comp4601.models.EntryDocument;
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

	private static MongoProvider<EntryDocument> entriesDatabase =
			new MongoProvider<>(getEntriesDatabaseConfiguration());
	
	private static MongoProvider<UserDocument> usersDatabase =
			new MongoProvider<>(getUsersDatabaseConfiguration());


	// PUBLIC INTERFACE =================================================================

	public void preprocess() {
		GenrePreprocessor genrePreprocessor = new GenrePreprocessor();
		genrePreprocessor.run();
	}
	
	public void upsert(UserDocument user) {
		usersDatabase.upsert(user);
	}
	
	public void upsert(PageDocument page) {
		pagesDatabase.upsert(page);
	}
	
	public void upsert(EntryDocument entry) {
		entriesDatabase.upsert(entry);
	}

	public Optional<PageDocument> findPage(String id) {
		return pagesDatabase.find(id, PageDocument.class);
	}
	
	public Optional<UserDocument> findUser(String id) {
		return usersDatabase.find(id, UserDocument.class);
	}
	
	public Optional<EntryDocument> findEntry(String userId, String pageId) {
		return entriesDatabase.find(userId + pageId, EntryDocument.class);
	}
	
	public List<EntryDocument> getUserEntries(String userId) {
		return entriesDatabase.find(EntryDocument.Fields.USER_ID, userId, EntryDocument.class);
	}
	
	public List<UserDocument> getUsersByCommunity(String community) {
		return usersDatabase.find(UserDocument.Fields.COMMUNITY, community, UserDocument.class);
	}
	
	public List<PageDocument> getAllPages() {
		return pagesDatabase.getAll(PageDocument.class);
	}
	
	public List<PageDocument> getPagesByCommunity(String community) {
		return pagesDatabase.find(PageDocument.Fields.GENRE, community, PageDocument.class);
	}
	
	public List<EntryDocument> getAllEntries() {
		return entriesDatabase.getAll(EntryDocument.class);
	}
	
	public List<UserDocument> getAllUsers() {
		return usersDatabase.getAll(UserDocument.class);
	}

	public void deletePage(String id) {
		Optional<PageDocument> page = findPage(id);

		pagesDatabase.delete(id);
		
		if (page.isEmpty()) {
			return;
		}
		
		page.get().getUsers().forEach(aPage -> {
			entriesDatabase.delete(aPage.getId() + id);
		});
	}
	
	public void deleteUser(String id) {
		Optional<UserDocument> user = findUser(id);

		usersDatabase.delete(id);
		
		if (user.isEmpty()) {
			return;
		}
		
		user.get().getPages().forEach(aUser -> {
			entriesDatabase.delete(aUser.getId() + id);
		});
	}
	
	public void deleteEntry(String userId, String pageId) {
		entriesDatabase.delete(userId + pageId);
	}

	public void reset() {
		pagesDatabase.reset();
		usersDatabase.reset();
		entriesDatabase.reset();
	}

	// PROVIDER CONFIGURATION ===========================================================

	private static final MongoDBConfig getPagesDatabaseConfiguration() {
		return new MongoDBConfig("localhost", 27017, "crawler", "pages");
	}

	private static final MongoDBConfig getEntriesDatabaseConfiguration() {
		return new MongoDBConfig("localhost", 27017, "crawler", "entries");
	}
	
	private static final MongoDBConfig getUsersDatabaseConfiguration() {
		return new MongoDBConfig("localhost", 27017, "crawler", "users");
	}
}
