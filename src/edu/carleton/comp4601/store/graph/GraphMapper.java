package edu.carleton.comp4601.store.graph;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.DefaultAttribute;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphExporter;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.VertexProvider;
import org.json.JSONObject;

import edu.carleton.comp4601.helpers.Encoder;
import edu.carleton.comp4601.models.WebDocument;
import edu.carleton.comp4601.store.ApplicationSpecificMapper;

public final class GraphMapper extends ApplicationSpecificMapper implements DocumentToGraphMapper<WebDocument, DefaultEdge> {
	private static final String JSON_ATTRIBUTE = "json_base64";

	// PROVIDERS ========================================================================

	private final ComponentNameProvider<WebDocument> VertexIdProvider =
			new ComponentNameProvider<WebDocument>() {

		public String getName(WebDocument doc) {
			return String.valueOf(doc.getId());
		}

	};

	private final ComponentNameProvider<WebDocument> VertexLabelProvider =
			new ComponentNameProvider<WebDocument>() {

		public String getName(WebDocument doc) {
			return doc.getURL().toString();
		}

	};

	private final ComponentAttributeProvider<WebDocument> VertexAttributeProvider =
			new ComponentAttributeProvider<WebDocument>() {

		@Override
		public Map<String, Attribute> getComponentAttributes(WebDocument doc) {
			Map<String, Attribute> map = new HashMap<>();

			// Saving memory by not using backslashes.
			String unescapedJSONString = String.valueOf(doc.toJSON());
			String base64Encoded = Encoder.encodeAsBase64(unescapedJSONString);

			Attribute encodedVertexData = DefaultAttribute.createAttribute(base64Encoded);
			map.put(JSON_ATTRIBUTE, encodedVertexData);

			return map;
		}

	};

	private final VertexProvider<WebDocument> VertexBuilder =
			new VertexProvider<WebDocument>() {

		@Override
		public WebDocument buildVertex(String id, Map<String, Attribute> attributes) {
			String encodedVertexData = attributes.get(JSON_ATTRIBUTE).getValue();
			String decodedVertexData = Encoder.decodeFromBase64(encodedVertexData);
			JSONObject jsonRepresentation = new JSONObject(decodedVertexData);

			String typeName = jsonRepresentation.getString(WebDocument.TYPE_FIELD);
			Class<? extends WebDocument> Clazz = classForTypeName(typeName);

			try {
				return Clazz.getDeclaredConstructor(JSONObject.class).newInstance(jsonRepresentation);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	};

	private final EdgeProvider<WebDocument, DefaultEdge> EdgeBuilder =
			new EdgeProvider<WebDocument, DefaultEdge>() {

		@Override
		public DefaultEdge buildEdge(WebDocument from, WebDocument to, String label,
				Map<String, Attribute> attributes) {
			return new DefaultEdge();
		}

	};

	// I/O ==============================================================================

	private final GraphExporter<WebDocument, DefaultEdge> exporter = 
			new DOTExporter<WebDocument, DefaultEdge>(
					VertexIdProvider,
					VertexLabelProvider,
					null,
					VertexAttributeProvider,
					null
				);

	private final GraphImporter<WebDocument, DefaultEdge> importer = 
			new DOTImporter<WebDocument, DefaultEdge>(
					VertexBuilder,
					EdgeBuilder
				);

	// PUBLIC INTERFACE =================================================================
	
	@Override
	public GraphExporter<WebDocument, DefaultEdge> getExporter() {
		return exporter;
	}

	@Override
	public GraphImporter<WebDocument, DefaultEdge> getImporter() {
		return importer;
	}

}
