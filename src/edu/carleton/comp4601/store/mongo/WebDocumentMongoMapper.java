package edu.carleton.comp4601.store.mongo;

import java.lang.reflect.InvocationTargetException;

import org.bson.Document;
import org.json.JSONObject;

import edu.carleton.comp4601.models.WebDocument;
import edu.carleton.comp4601.store.ApplicationSpecificMapper;
import edu.carleton.comp4601.store.DocumentMapper;

public final class WebDocumentMongoMapper extends ApplicationSpecificMapper implements DocumentMapper<WebDocument, Document> {

	@Override
	public Document serialize(WebDocument input) {
		return Document.parse(input.toJSON().toString());
	}

	@Override
	public WebDocument deserialize(Document input) 
			throws 
			InstantiationException, 
			IllegalAccessException, 
			IllegalArgumentException, 
			InvocationTargetException, 
			NoSuchMethodException, 
			SecurityException {

		JSONObject parsedJSON = new JSONObject(input.toJson());
		
		String typeName = parsedJSON.getString(WebDocument.TYPE_FIELD);
		Class<? extends WebDocument> Clazz = classForTypeName(typeName);

		return Clazz.getDeclaredConstructor(JSONObject.class).newInstance(parsedJSON);
	}
}
