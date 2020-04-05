package edu.carleton.comp4601.utility;

import java.util.Optional;

import edu.carleton.comp4601.models.PageDocument;
import edu.carleton.comp4601.models.UserDocument;
import edu.carleton.comp4601.store.DataCoordinator;

public final class ContentAugmenter {
	private static final DataCoordinator dataCoordinator = DataCoordinator.getInstance();
	
	public static String wrap(String userId, String pageId) {
		Optional<PageDocument> pageDocument = dataCoordinator.findPage(pageId);
		
		if (pageDocument.isEmpty()) {
			return "Page not found.";
		}
		
		String advertisingCategory = "none";
		
		Optional<UserDocument> user = dataCoordinator.findUser(userId);
		
		if (user.isPresent()) {
			advertisingCategory = user.get().getCommunity().orElse("none");
		}
		
		String output = "";
		output += "<frameset cols=\"*,25%\">";
		output += "<frame src=\"" + pageDocument.get().getURL().getURL() + "\">";
		output += "<frame src=\"" + "../../advertising/" + advertisingCategory + "\">";
		output += "</frameset>";
		
		return output;
	}
}
