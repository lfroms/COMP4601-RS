package edu.carleton.comp4601.analyzers;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.carleton.comp4601.analyzers.utility.StopWords;
import edu.carleton.comp4601.models.PageDocument;
import edu.carleton.comp4601.store.DataCoordinator;

public final class GenrePreprocessor {
	public final static class Genre {
		public static final String HORROR = "horror";
		public static final String THRILLER = "thriller";
		public static final String MYSTERY = "mystery";
		public static final String DRAMA = "drama";
		public static final String CRIME = "crime";
		public static final String ACTION = "action";
		public static final String COMEDY = "comedy";
		public static final String MUSICAL = "musical";
		public static final String BIOGRAPHY = "biography";
		public static final String WAR = "war";
		
		public static final Set<String> all = new HashSet<>() {
			private static final long serialVersionUID = -7807216999871691949L;
		{
			add(HORROR);
			add(THRILLER);
			add(MYSTERY);
			add(DRAMA);
			add(CRIME);
			add(ACTION);
			add(COMEDY);
			add(MUSICAL);
			add(BIOGRAPHY);
			add(WAR);
		}};
	}
	
	private static final Map<String, String> dictionary = new HashMap<>() {
		private static final long serialVersionUID = 2298946785778182251L;
	{
		put("scary", Genre.HORROR);
		put("scare", Genre.HORROR);
		put("horror", Genre.HORROR);
		put("gore", Genre.HORROR);
		put("thriller", Genre.THRILLER);
		put("suspense", Genre.THRILLER);
		put("mystery", Genre.MYSTERY);
		put("solve", Genre.MYSTERY);
		put("drama", Genre.DRAMA);
		put("dramatic", Genre.DRAMA);
		put("Oscar", Genre.DRAMA);
		put("crime", Genre.CRIME);
		put("murder", Genre.CRIME);
		put("murderer", Genre.CRIME);
		put("murders", Genre.CRIME);
		put("murderous", Genre.CRIME);
		put("detective", Genre.CRIME);
		put("detectives", Genre.CRIME);
		put("action", Genre.ACTION);
		put("stunts", Genre.ACTION);
		put("comedy", Genre.COMEDY);
		put("funny", Genre.COMEDY);
		put("laugh", Genre.COMEDY);
		put("laughed", Genre.COMEDY);
		put("musical", Genre.MUSICAL);
		put("Broadway", Genre.MUSICAL);
		put("biographical", Genre.BIOGRAPHY);
		put("biograpy", Genre.BIOGRAPHY);
		put("autobiography", Genre.BIOGRAPHY);
		put("history", Genre.BIOGRAPHY);
		put("war", Genre.WAR);
		put("soldiers", Genre.WAR);
	}};
	
	
	private static final DataCoordinator dataCoordinator = DataCoordinator.getInstance();

	public void run() {
		List<PageDocument> pages = dataCoordinator.getAllPages();

		pages.parallelStream().forEach(page -> {
			HashMap<String, Integer> scoresForPage = calculateGenresForPage(page);
			String highestGenre = getHighestScoringGenre(scoresForPage);
			String secondHighestGenre = getSecondHighestScoringGenre(scoresForPage, highestGenre);
			
			page.setGenre(highestGenre);
			page.setSecondGenre(secondHighestGenre);
			dataCoordinator.upsert(page);
		});
	}

	// PRIVATE HELPERS ==================================================================
	
	private HashMap<String, Integer> calculateGenresForPage(PageDocument page) {
		String textWithRemovedStopwords = page.getContent();
		
		for (String stopWord: StopWords.all) {
			textWithRemovedStopwords.replaceAll("(?i)" + stopWord, " ");
		}
		
		String[] inputText = textWithRemovedStopwords.trim().replaceAll(" +", " ").split(" ");
		
		HashMap<String, Integer> scores = new HashMap<>() {
			private static final long serialVersionUID = 1L;
		{
			Genre.all.forEach(genre -> {
				put(genre, 0);
			});
		}};
		
		for (String word: inputText) {
			if (dictionary.containsKey(word)) {
				String genre = dictionary.get(word);
				
				Integer newScore = scores.get(genre);
				newScore += 1;
				
				scores.put(genre, newScore);
			}
		}
		
		return scores;
	}
	
	private String getHighestScoringGenre(HashMap<String, Integer> scores) {
		return Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
	}
	
	private String getSecondHighestScoringGenre(HashMap<String, Integer> scores, String highestScoringGenre) {
		scores.remove(highestScoringGenre);
		
		return Collections.max(scores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
	}
}
