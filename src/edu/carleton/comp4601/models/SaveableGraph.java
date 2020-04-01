package edu.carleton.comp4601.models;

import org.json.JSONObject;

public final class SaveableGraph implements Identifiable, JSONSerializable {
	private final Integer id;
	private final String serializedData;
	
	public SaveableGraph(Integer id, String serializedData) {
		this.id = id;
		this.serializedData = serializedData;
	}
	
	// JSON SERIALIZATION ===============================================================
	
	public SaveableGraph(JSONObject object) {
		this.id = object.getInt(Fields.ID);
		this.serializedData = object.getString(Fields.DATA);
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		
		object
			.put(Fields.ID, id)
			.put(Fields.DATA, serializedData);
		
		return object;
	}
	
	// GETTERS ==========================================================================

	@Override
	public Integer getId() {
		return id;
	}

	public String getSerializedData() {
		return serializedData;
	}

	// FIELD NAMES ======================================================================
	
	private final static class Fields {
		public final static String ID = "id";
		public final static String DATA = "data";
	}
}
