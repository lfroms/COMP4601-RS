package edu.carleton.comp4601.analyzers;

import java.util.List;

import edu.carleton.comp4601.models.PageDocument;
import edu.carleton.comp4601.store.DataCoordinator;

public final class GenrePreprocessor {
	private static final String[] GENRES = { "comedy", "drama", "action", "adventure", "documentary", "romance", "thriller", "horror" };
	private static final DataCoordinator dataCoordinator = DataCoordinator.getInstance();
	
	public void run() {
		List<PageDocument> pages = dataCoordinator.getAllPages();
		
		pages.forEach(page -> {
			page.setGenre(GENRES[0]);
			
			dataCoordinator.upsert(page);
		});
	}
	
	// PRIVATE HELPERS ==================================================================
}
