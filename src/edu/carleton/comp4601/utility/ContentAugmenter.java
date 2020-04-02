package edu.carleton.comp4601.utility;

import java.util.Optional;

import edu.carleton.comp4601.models.PageDocument;
import edu.carleton.comp4601.store.DataCoordinator;

public final class ContentAugmenter {
	private static final DataCoordinator dataCoordinator = DataCoordinator.getInstance();
	
	public static String wrap(String userId, String pageId) {
		Optional<PageDocument> pageDocument = dataCoordinator.findPage(pageId);
		
		if (pageDocument.isEmpty()) {
			return "Page not found.";
		}
		
		// TODO: - Get preferred genre.
		String advertisingCategory = "comedy";
		
		String output = "";
		output += "<frameset cols=\"*,25%\">";
		output += "<frame src=\"" + pageDocument.get().getURL().getURL() + "\">";
		output += "<frame src=\"" + "../../advertising/" + advertisingCategory + "\">";
		output += "</frameset>";
		
		return output;
	}
}
