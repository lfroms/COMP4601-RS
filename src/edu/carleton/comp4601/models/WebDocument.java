package edu.carleton.comp4601.models;

import org.json.JSONObject;

import edu.uci.ics.crawler4j.url.WebURL;

public abstract class WebDocument implements Identifiable, Locatable, JSONSerializable {
	private final String id;
	private final String title;
	private final WebURL url;

	public WebDocument(String id, String title, WebURL url) {
		this.id = id;
		this.title = title;
		this.url = url;
	}

	// JSON SERIALIZATION ===============================================================

	public WebDocument(JSONObject object) {
		WebURL newUrl = new WebURL();
		newUrl.setURL(object.getString(Fields.URL));
		newUrl.setDocid(object.getInt(Fields.ID));

		this.id = object.getString(Fields.ID);
		this.title = object.getString(Fields.TITLE);
		this.url = newUrl;
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		
		object
			.put(Fields.ID, id)
			.put(Fields.TITLE, title)
			.put(Fields.URL, url);
		
		return object;
	}

	// GETTERS ==========================================================================

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public WebURL getURL() {
		return url;
	}

	// FIELD NAMES ======================================================================

	private static class Fields {
		public static final String ID = "id";
		public static final String TITLE = "title";
		public static final String URL = "url";
	}
}
