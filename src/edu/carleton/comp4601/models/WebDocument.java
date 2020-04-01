package edu.carleton.comp4601.models;

import org.json.JSONObject;

import edu.uci.ics.crawler4j.url.WebURL;

public abstract class WebDocument implements Identifiable, Locatable, JSONSerializable {
	private final Integer id;
	private final String title;
	private final String typeName;
	private final WebURL url;

	public WebDocument(Integer id, String title, WebURL url) {
		this.id = id;
		this.title = title;
		this.typeName = getTypeName();
		this.url = url;
	}

	// JSON SERIALIZATION
	// ===============================================================

	public WebDocument(JSONObject object) {
		WebURL newUrl = new WebURL();
		newUrl.setURL(object.getString(Fields.URL));
		newUrl.setDocid(object.getInt(Fields.ID));
		newUrl.setParentDocid(object.getInt(Fields.PARENT_ID));

		this.id = object.getInt(Fields.ID);
		this.title = object.getString(Fields.TITLE);
		this.typeName = getTypeName();
		this.url = newUrl;
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		
		object
			.put(Fields.ID, id)
			.put(Fields.PARENT_ID, getParentId())
			.put(Fields.TITLE, title)
			.put(TYPE_FIELD, typeName)
			.put(Fields.URL, url);
		
		return object;
	}

	// GETTERS
	// ==========================================================================

	public Integer getId() {
		return id;
	}

	public Integer getParentId() {
		return url.getParentDocid();
	}

	public String getTitle() {
		return title;
	}

	public WebURL getURL() {
		return url;
	}

	public abstract String getContent();

	public abstract String getTypeName();

	// FIELD NAMES
	// ======================================================================

	private static class Fields {
		public static final String ID = "id";
		public static final String TITLE = "title";
		public static final String PARENT_ID = "parent_id";
		public static final String URL = "url";
	}
}
