package edu.carleton.comp4601.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.store.DataCoordinator;
import edu.uci.ics.crawler4j.url.WebURL;

public final class PageDocument extends WebDocument implements Identifiable {
	private List<String> paragraphs;
	private List<String> userIds;
	private String genre = null;
	private String secondGenre = null;

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
		
		if (object.has(Fields.GENRE)) {
			this.genre = object.getString(Fields.GENRE);
		}

		if (object.has(Fields.SECOND_GENRE)) {
			this.secondGenre = object.getString(Fields.SECOND_GENRE);
		}
	}

	@Override
	public JSONObject toJSON() {
		JSONObject object = super.toJSON();

		object
			.put(Fields.PARAGRAPHS, paragraphs)
			.put(Fields.GENRE, genre)
			.put(Fields.SECOND_GENRE, secondGenre)
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
		Elements linkElements = document.select("a[href]").not("[href*=http]");
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
	
	public List<String> getParagraphs() {
		return paragraphs;
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
	
	public List<EntryDocument> getEntries(Document document) {
		Elements htmlElements = document.body().children();
		Iterator<Element> it = htmlElements.iterator();
		
		List<EntryDocument> entries = new ArrayList<>();

		while (it.hasNext()) {
			Element userLink = it.next();
			assert userLink.tag().getName().equalsIgnoreCase("a");
			Element temp = it.next();
			assert temp.tag().getName().equalsIgnoreCase("br");
			StringBuilder reviewTextBuilder = new StringBuilder();
			Element paragraph = null;
			
			do {
				paragraph = it.next();

				reviewTextBuilder.append(paragraph.text()).append("\n\n");
			} while (it.hasNext() && paragraph.tag().getName().equals("p"));
			
			entries.add(new EntryDocument(userLink.text(), super.getId(), reviewTextBuilder.toString()));
		}

		return entries;
	}
	
	public String getGenre() {
		return genre;
	}
	
	public String getSecondaryGenre() {
		return secondGenre;
	}
	
	// SETTERS ==========================================================================
	
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	public void setSecondGenre(String secondGenre) {
		this.secondGenre = secondGenre;
	}

	// FIELD NAMES ======================================================================

	private static class Fields {
		public static final String PARAGRAPHS = "paragraphs";
		public static final String GENRE = "genre";
		public static final String SECOND_GENRE = "second_genre";
		public static final String USER_IDS = "user_ids";
	}
}
