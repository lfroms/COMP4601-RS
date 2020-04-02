package edu.carleton.comp4601.models;

import org.json.JSONObject;

import edu.uci.ics.crawler4j.url.WebURL;

public abstract class WebDocument extends StorableDocument implements Locatable {
	private final WebURL url;

	public WebDocument(String id, WebURL url) {
		super(id);
		
		this.url = url;
	}

	// JSON SERIALIZATION ===============================================================

	public WebDocument(JSONObject object) {
		super(object);
		
		WebURL newUrl = new WebURL();
		newUrl.setURL(object.getString(Fields.URL));

		this.url = newUrl;
	}

	public JSONObject toJSON() {
		JSONObject object = super.toJSON();
		
		object
			.put(Fields.URL, url);
		
		return object;
	}

	// GETTERS ==========================================================================

	public WebURL getURL() {
		return url;
	}

	// FIELD NAMES ======================================================================

	private static class Fields {
		public static final String URL = "url";
	}
}
