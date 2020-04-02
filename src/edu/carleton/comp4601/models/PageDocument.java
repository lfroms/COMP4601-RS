package edu.carleton.comp4601.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.store.DataCoordinator;
import edu.uci.ics.crawler4j.url.WebURL;

public final class PageDocument extends WebDocument implements Identifiable {
	private List<String> paragraphs;
	private List<String> userIds;
	private String genre;

	private static DataCoordinator dataCoordinator = DataCoordinator.getInstance();
	
	public PageDocument(WebURL url, String htmlParseDataText) {
		super(getTitleFromDocument(Jsoup.parse(htmlParseDataText)), url);

		Document doc = Jsoup.parse(htmlParseDataText);

		this.paragraphs = getParagraphsFromDocument(doc);
		this.userIds = getUserIdsFromDocument(doc);
	}

	// JSON SERIALIZATION ===============================================================

	public PageDocument(JSONObject object) {
		super(object);

		this.paragraphs = parseJSONStringArray(object, Fields.PARAGRAPHS);
		this.userIds = parseJSONStringArray(object, Fields.USER_IDS);
		this.genre = object.getString(Fields.GENRE);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject object = super.toJSON();

		object
			.put(Fields.PARAGRAPHS, paragraphs)
			.put(Fields.GENRE, genre)
			.put(Fields.USER_IDS, userIds);

		return object;
	}

	// HTML PARSING HELPERS =============================================================

	private static String getTitleFromDocument(Document document) {
		return document.title();
	}

	private static List<String> getParagraphsFromDocument(Document document) {
		Elements paragraphElements = document.select("p");
		return paragraphElements.eachText();
	}
	
	private static List<String> getUserIdsFromDocument(Document document) {
		Elements linkElements = document.select("a[href*=users]");
		return linkElements.eachText().stream().map(text -> text.toUpperCase().trim()).collect(Collectors.toList());
	}

	private static List<String> parseJSONStringArray(JSONObject object, String fieldName) {
		List<String> parsedValues = new ArrayList<>();
		JSONArray rawValues = object.getJSONArray(fieldName);

		for (int i = 0; i < rawValues.length(); i++)
			parsedValues.add(rawValues.getString(i));

		return parsedValues;
	}

	// GETTERS ==========================================================================

	public String getContent() {
		return String.join(" ", paragraphs).strip();
	}

	public List<String> getUserIds() {
		return userIds;
	}
	
	public List<UserDocument> getUsers() {
		List<UserDocument> collection = new ArrayList<>();
		
		userIds.forEach(pageId -> {
			Optional<UserDocument> page = dataCoordinator.findUser(pageId);
			
			if (page.isPresent()) {
				collection.add(page.get());
			}
		});
		
		return collection;
	}
	
	public String getGenre() {
		return genre;
	}
	
	// SETTERS ==========================================================================
	
	public void setGenre(String genre) {
		this.genre = genre;
	}

	// FIELD NAMES ======================================================================

	private static class Fields {
		public static final String PARAGRAPHS = "paragraphs";
		public static final String GENRE = "genre";
		public static final String USER_IDS = "user_ids";
	}
}
