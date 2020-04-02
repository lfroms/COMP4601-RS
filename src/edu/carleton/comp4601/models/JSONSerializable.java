package edu.carleton.comp4601.models;

import org.json.JSONObject;

public interface JSONSerializable {
	/**
	 * 
	 * @return A JSON representation
	 */
	public JSONObject toJSON();	
}
