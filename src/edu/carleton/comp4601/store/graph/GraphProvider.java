package edu.carleton.comp4601.store.graph;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.ImportException;

import edu.carleton.comp4601.models.Identifiable;
import edu.carleton.comp4601.models.Locatable;
import edu.carleton.comp4601.store.Storable;

public final class GraphProvider<DocumentType extends Identifiable & Locatable> implements Storable<DocumentType>  {
	private final Supplier<? extends DocumentToGraphMapper<DocumentType, DefaultEdge>> mapperConstructor;
	
	private Graph<DocumentType, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);

	public GraphProvider(Supplier<? extends DocumentToGraphMapper<DocumentType, DefaultEdge>> mapperConstructor) {
		this.mapperConstructor = Objects.requireNonNull(mapperConstructor);
	}

	@Override
	public void upsert(DocumentType input) {
		if (graph.containsVertex(input)) {
			graph.removeVertex(input);
		}
		
		graph.addVertex(input);
		addEdgeIfParentExists(input);
	}

	@Override
	public Optional<DocumentType> find(Integer id) {
		return graph.vertexSet().stream().filter(x -> x.getId() == id).findFirst();
	}
	
	@Override
	public void delete(Integer id) {
		try {
			graph.removeVertex(find(id).get());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not find graph node with id " + id + ". Skipping...");
		}
	}
	
	public Map<Integer, Double> getRanksForAllObjects() {
		return PageRanker.computeRanks(graph);
	}
	
	// SERIALIZATION ====================================================================
	
	public final String toGraphViz() throws ExportException {
		Writer writer = new StringWriter();
		mapperConstructor.get().getExporter().exportGraph(graph, writer);
        
        return writer.toString();
	}
	
	public final Boolean setDataUsingGraphViz(String serializedData) {
		try {
			reset();
			mapperConstructor.get().getImporter().importGraph(graph, new StringReader(serializedData));
			return true;
			
		} catch (ImportException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	// PRIVATE HELPERS ==================================================================
	
	private final Boolean addEdgeIfParentExists(DocumentType vertex) {
		Optional<DocumentType> parentVertexOptional = find(vertex.getURL().getParentDocid());

		try {
			DocumentType parentVertex = parentVertexOptional.get();
			graph.addEdge(parentVertex, vertex);
			
			return true;

		} catch(NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public void reset() {
		graph = new DirectedMultigraph<>(DefaultEdge.class);
	}
}
