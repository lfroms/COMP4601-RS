package edu.carleton.comp4601.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import edu.uci.ics.crawler4j.url.WebURL;

public final class HypertextDocument extends WebDocument {
	private List<String> paragraphs;

	public HypertextDocument(Integer id, WebURL url, String htmlParseDataText) {

		super(id, getTitleFromDocument(Jsoup.parse(htmlParseDataText)), url);

		Document doc = Jsoup.parse(htmlParseDataText);

		this.paragraphs = getParagraphsFromDocument(doc);
	}

	// JSON SERIALIZATION
	// ===============================================================

	public HypertextDocument(JSONObject object) {
		super(object);

		this.paragraphs = parseJSONStringArray(object, Fields.PARAGRAPHS);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject object = super.toJSON();

		object.put(Fields.PARAGRAPHS, paragraphs);

		return object;
	}

	// HTML PARSING HELPERS
	// =============================================================

	private static String getTitleFromDocument(Document document) {
		return document.title();
	}

	private static List<String> getParagraphsFromDocument(Document document) {
		Elements paragraphElements = document.select("p");

		return paragraphElements.eachText();
	}

	private static List<String> parseJSONStringArray(JSONObject object, String fieldName) {
		List<String> parsedValues = new ArrayList<>();
		JSONArray rawValues = object.getJSONArray(fieldName);

		for (int i = 0; i < rawValues.length(); i++)
			parsedValues.add(rawValues.getString(i));

		return parsedValues;
	}

	// GETTERS
	// ==========================================================================

	@Override
	public String getContent() {
		return String.join(" ", paragraphs).substring(0, 200);
	}

	// FIELD NAMES
	// ======================================================================

	private static class Fields {
		public static final String PARAGRAPHS = "paragraphs";
	}

	public static final String TYPE_NAME = "hypertext";

	@Override
	public String getTypeName() {
		return TYPE_NAME;
	}
}
