package edu.carleton.comp4601.models;

import java.util.Optional;

import org.json.JSONObject;

import edu.carleton.comp4601.store.DataCoordinator;

public final class EntryDocument extends StorableDocument implements Identifiable {
	private String text;
	private String userId;
	private String pageId;
	private Boolean sentimentIsPositive = null;

	private static DataCoordinator dataCoordinator = DataCoordinator.getInstance();
	
	public EntryDocument(String userId, String pageId, String text) {
		super(userId + pageId);

		this.userId = userId;
		this.pageId = pageId;
		this.text = text;
	}

	// JSON SERIALIZATION ===============================================================

	public EntryDocument(JSONObject object) {
		super(object);

		this.userId = object.getString(Fields.USER_ID);
		this.pageId = object.getString(Fields.PAGE_ID);
		this.text = object.getString(Fields.TEXT);
		
		if (object.has(Fields.SENTIMENT_IS_POSITIVE)) {
			this.sentimentIsPositive = object.getBoolean(Fields.SENTIMENT_IS_POSITIVE);
		}
	}

	@Override
	public JSONObject toJSON() {
		JSONObject object = super.toJSON();

		object
			.put(Fields.USER_ID, userId)
			.put(Fields.PAGE_ID, pageId)
			.put(Fields.SENTIMENT_IS_POSITIVE, sentimentIsPositive)
			.put(Fields.TEXT, text);

		return object;
	}

	// GETTERS ==========================================================================

	public String getUserId() {
		return userId;
	}
	
	public UserDocument getUser() {
		return dataCoordinator.findUser(userId).get();
	}
	
	public String getPageId() {
		return pageId;
	}
	
	public PageDocument getPage() {
		return dataCoordinator.findPage(pageId).get();
	}
	
	public String getText() {
		return text;
	}
	
	public Optional<Boolean> getSentiment() {
		return Optional.ofNullable(sentimentIsPositive);
	}
	
	// SETTERS ==========================================================================
	
	public void setSentiment(Boolean sentiment) {
		this.sentimentIsPositive = sentiment;
	}

	// FIELD NAMES ======================================================================

	public static class Fields {
		public static final String USER_ID = "user_id";
		public static final String PAGE_ID = "page_id";
		public static final String TEXT = "text";
		public static final String SENTIMENT_IS_POSITIVE = "sentiment_is_positive";
	}
}
