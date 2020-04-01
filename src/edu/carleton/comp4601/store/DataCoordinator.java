package edu.carleton.comp4601.store;

import java.util.ArrayList;
import java.util.Optional;

import org.jgrapht.io.ExportException;

import edu.carleton.comp4601.models.SaveableGraph;
import edu.carleton.comp4601.models.WebDocument;
import edu.carleton.comp4601.store.graph.GraphMapper;
import edu.carleton.comp4601.store.graph.GraphProvider;
import edu.carleton.comp4601.store.mongo.GraphMongoMapper;
import edu.carleton.comp4601.store.mongo.MongoDBConfig;
import edu.carleton.comp4601.store.mongo.MongoProvider;
import edu.carleton.comp4601.store.mongo.WebDocumentMongoMapper;

public final class DataCoordinator implements Storable<WebDocument> {
	private static DataCoordinator singleInstance = null;

	public static DataCoordinator getInstance() {
		if (singleInstance == null) {
			singleInstance = new DataCoordinator();
		}

		return singleInstance;
	}

	// CONSTANTS
	// ========================================================================

	private static final Integer GRAPH_DB_ID = 1;

	// STORABLE INSTANCES
	// ===============================================================

	private static MongoProvider<WebDocument> documentsDatabase = new MongoProvider<>(WebDocumentMongoMapper::new,
			getDocumentsDatabaseConfig());

	private static Storable<SaveableGraph> graphsDatabase = new MongoProvider<>(GraphMongoMapper::new,
			getGraphsDatabaseConfig());

	private static GraphProvider<WebDocument> graphProvider = new GraphProvider<>(GraphMapper::new);

	// PUBLIC INTERFACE
	// =================================================================

	@Override
	public void upsert(WebDocument input) {
		graphProvider.upsert(input);
		documentsDatabase.upsert(input);
	}

	@Override
	public Optional<WebDocument> find(Integer id) {
		return documentsDatabase.find(id);
	}

	public ArrayList<WebDocument> getAll() {
		return documentsDatabase.getAll();
	}

	@Override
	public void delete(Integer id) {
		documentsDatabase.delete(id);
		graphProvider.delete(id);
	}

	@Override
	public void reset() {
		graphProvider.reset();
		documentsDatabase.reset();
		graphsDatabase.reset();
	}

	public void loadPersistedData() {
		loadGraphFromDatabase();
	}

	public void processAndStoreData() {
		System.out.println("NOTICE: Indexing and persisting documents...");
		saveGraphToDatabase();
	}

	// PRIVATE HELPERS
	// ================================================================
	private final void saveGraphToDatabase() {
		String serializedGraph;

		try {
			serializedGraph = graphProvider.toGraphViz();

		} catch (ExportException e) {
			e.printStackTrace();
			System.err.println("Could not save graph data.");

			return;
		}

		SaveableGraph saveableGraph = new SaveableGraph(GRAPH_DB_ID, serializedGraph);

		graphsDatabase.upsert(saveableGraph);
	}

	private final void loadGraphFromDatabase() {
		Optional<SaveableGraph> savedGraph = graphsDatabase.find(GRAPH_DB_ID);

		if (savedGraph.isEmpty()) {
			return;
		}

		String serializedData = savedGraph.get().getSerializedData();
		graphProvider.setDataUsingGraphViz(serializedData);
	}

	// PROVIDER CONFIGURATION
	// ===========================================================

	private static final MongoDBConfig getDocumentsDatabaseConfig() {
		return new MongoDBConfig("localhost", 27017, "crawler", "documents");
	}

	private static final MongoDBConfig getGraphsDatabaseConfig() {
		return new MongoDBConfig("localhost", 27017, "crawler", "graphs");
	}
}
