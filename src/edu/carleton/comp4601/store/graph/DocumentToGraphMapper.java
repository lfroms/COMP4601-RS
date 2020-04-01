package edu.carleton.comp4601.store.graph;

import org.jgrapht.io.GraphExporter;
import org.jgrapht.io.GraphImporter;

import edu.carleton.comp4601.models.Identifiable;

public interface DocumentToGraphMapper<DocumentType extends Identifiable, EdgeType> {
	
	/**
	 * 
	 * @return A GraphExporter
	 */
	public GraphExporter<DocumentType, EdgeType> getExporter();
	
	/**
	 * 
	 * @return A GraphImporter
	 */
	public GraphImporter<DocumentType, EdgeType> getImporter();

}
