package edu.carleton.comp4601.models;

import org.json.JSONObject;

import edu.uci.ics.crawler4j.url.WebURL;

public abstract class WebDocument implements Identifiable, Locatable, JSONSerializable {
	private final String id;
	private final WebURL url;

	public WebDocument(String id, WebURL url) {
		this.id = id;
		this.url = url;
	}

	// JSON SERIALIZATION ===============================================================

	public WebDocument(JSONObject object) {
		WebURL newUrl = new WebURL();
		newUrl.setURL(object.getString(Fields.URL));

		this.id = object.getString(Fields.ID);
		this.url = newUrl;
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		
		object
			.put(Fields.ID, id)
			.put(Fields.URL, url);
		
		return object;
	}

	// GETTERS ==========================================================================

	public String getId() {
		return id;
	}

	public WebURL getURL() {
		return url;
	}

	// FIELD NAMES ======================================================================

	private static class Fields {
		public static final String ID = "id";
		public static final String URL = "url";
	}
}
