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

public final class UserDocument extends WebDocument implements Identifiable {
	private List<String> pageIds;
	private String community = null;
	
	private static DataCoordinator dataCoordinator = DataCoordinator.getInstance();

	public UserDocument(WebURL url, String htmlParseDataText) {
		super(getTitleFromDocument(Jsoup.parse(htmlParseDataText)), url);

		Document doc = Jsoup.parse(htmlParseDataText);

		this.pageIds = getPageIdsFromDocument(doc);
	}

	// JSON SERIALIZATION ===============================================================

	public UserDocument(JSONObject object) {
		super(object);

		this.pageIds = parseJSONStringArray(object, Fields.PAGE_IDS);
		
		if (object.has(Fields.COMMUNITY)) {
			this.community = object.getString(Fields.COMMUNITY);
		}
	}

	@Override
	public JSONObject toJSON() {
		JSONObject object = super.toJSON();

		object.put(Fields.PAGE_IDS, pageIds).put(Fields.COMMUNITY, community);

		return object;
	}

	// HTML PARSING HELPERS =============================================================

	private static String getTitleFromDocument(Document document) {
		return document.title();
	}

	
	private static List<String> getPageIdsFromDocument(Document document) {
		Elements linkElements = document.select("a");
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

	public List<String> getPageIds() {
		return pageIds;
	}
	
	public List<PageDocument> getPages() {
		List<PageDocument> collection = new ArrayList<>();
		
		pageIds.forEach(pageId -> {
			Optional<PageDocument> page = dataCoordinator.findPage(pageId);
			
			if (page.isPresent()) {
				collection.add(page.get());
			}
		});
		
		return collection;
	}
	
	public List<EntryDocument> getEntries() {
		return dataCoordinator.getUserEntries(getId());
	}
	
	public Optional<String> getCommunity() {
		return Optional.ofNullable(community);
	}
	
	// SETTERS ==========================================================================
	
	public void setCommunity(String community) {
		this.community = community;
	}

	// FIELD NAMES ======================================================================

	private static class Fields {
		public static final String PAGE_IDS = "page_ids";
		public static final String COMMUNITY = "community";
	}
}
