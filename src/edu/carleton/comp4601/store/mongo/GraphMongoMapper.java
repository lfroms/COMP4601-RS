package edu.carleton.comp4601.store.mongo;

import java.lang.reflect.InvocationTargetException;

import org.bson.Document;
import org.json.JSONObject;

import edu.carleton.comp4601.models.SaveableGraph;
import edu.carleton.comp4601.store.ApplicationSpecificMapper;
import edu.carleton.comp4601.store.DocumentMapper;

public final class GraphMongoMapper extends ApplicationSpecificMapper implements DocumentMapper<SaveableGraph, Document> {

	@Override
	public Document serialize(SaveableGraph input) {
		return Document.parse(input.toJSON().toString());
	}

	@Override
	public SaveableGraph deserialize(Document input) 
			throws 
			InstantiationException, 
			IllegalAccessException, 
			IllegalArgumentException, 
			InvocationTargetException, 
			NoSuchMethodException, 
			SecurityException {

		JSONObject parsedJSON = new JSONObject(input.toJson());
		
		return new SaveableGraph(parsedJSON);
	}
}
