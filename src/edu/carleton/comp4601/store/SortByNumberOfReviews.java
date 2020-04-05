package edu.carleton.comp4601.store;

import java.util.Comparator;

import edu.carleton.comp4601.models.PageDocument;

final class SortByNumberOfReviews implements Comparator<PageDocument> {
	public int compare(PageDocument a, PageDocument b) {
		return b.getUserIds().size() - a.getUserIds().size();
	} 
}
