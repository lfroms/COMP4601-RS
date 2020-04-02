package edu.carleton.comp4601.models;

import org.json.JSONObject;

public abstract class StorableDocument implements Identifiable, JSONSerializable {
	private final String id;

	public StorableDocument(String id) {
		this.id = id;
	}

	// JSON SERIALIZATION ===============================================================

	public StorableDocument(JSONObject object) {
		this.id = object.getString(Fields.ID);
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		
		object
			.put(Fields.ID, id);
		
		return object;
	}

	// GETTERS ==========================================================================

	public String getId() {
		return id;
	}


	// FIELD NAMES ======================================================================

	private static class Fields {
		public static final String ID = "id";
	}
}
