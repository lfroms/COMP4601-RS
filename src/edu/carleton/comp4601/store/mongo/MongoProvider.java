package edu.carleton.comp4601.store.mongo;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import edu.carleton.comp4601.models.Identifiable;
import edu.carleton.comp4601.store.DocumentMapper;
import edu.carleton.comp4601.store.MappableProvider;
import edu.carleton.comp4601.store.Storable;

public final class MongoProvider<DocumentType extends Identifiable> extends MappableProvider<DocumentType, Document> implements Storable<DocumentType> {
	private final MongoClient mongoClient;
	private final MongoDatabase db;
	private final MongoCollection<Document> collection;

	private static final String SYSTEM_ID_FIELD = "_id";
	
	public MongoProvider(Supplier<? extends DocumentMapper<DocumentType, Document>> mapperConstructor, MongoDBConfig config) {
		super(mapperConstructor);
	
		this.mongoClient = new MongoClient(config.getHostname(), config.getPort());
		this.db = mongoClient.getDatabase(config.getDatabaseName());
		this.collection = db.getCollection(config.getCollectionName());
	}

	public final void upsert(DocumentType document) {
		Bson filter = Filters.eq(SYSTEM_ID_FIELD, document.getId());
		ReplaceOptions options = new ReplaceOptions().upsert(true);

		Document documentToSave = mapperConstructor.get().serialize(document);
		collection.replaceOne(filter, documentToSave, options);
	}

	public final Optional<DocumentType> find(Integer documentId) {
		FindIterable<Document> cursor = collection.find(new BasicDBObject(SYSTEM_ID_FIELD, documentId));
		MongoCursor<Document> c = cursor.iterator();

		if (!c.hasNext()) {
			return Optional.empty();
		}

		Document document = c.next();

		try {
			return Optional.of(mapperConstructor.get().deserialize(document));

		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	public final ArrayList<DocumentType> getAll() {
		ArrayList<DocumentType> output = new ArrayList<>();
		
		FindIterable<Document> cursor = collection.find();
		MongoCursor<Document> c = cursor.iterator();
		
		while (c.hasNext()) {
			Document obj = c.next();
			
			try {
				output.add(mapperConstructor.get().deserialize(obj));
			} catch (Exception e) {
				System.err.println("Could not deserialize document with id " + obj.getInteger(SYSTEM_ID_FIELD) + ". Skipping...");
				e.printStackTrace();
			}
		}
		
		return output;
	}

	@Override
	public void delete(Integer id) {
		Bson filter = Filters.eq(SYSTEM_ID_FIELD, id);
		collection.deleteOne(filter);
	}

	@Override
	public void reset() {
		collection.drop();
	}
}
