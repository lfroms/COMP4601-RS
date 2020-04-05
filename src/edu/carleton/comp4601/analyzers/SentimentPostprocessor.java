package edu.carleton.comp4601.analyzers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.carleton.comp4601.analyzers.GenrePreprocessor.Genre;
import edu.carleton.comp4601.analyzers.utility.StopWords;
import edu.carleton.comp4601.analyzers.utility.StringCleaner;
import edu.carleton.comp4601.models.EntryDocument;
import edu.carleton.comp4601.models.UserDocument;
import edu.carleton.comp4601.store.DataCoordinator;

public final class SentimentPostprocessor {
	private static final DataCoordinator dataCoordinator = DataCoordinator.getInstance();
	
	public static int lastRunId = 0;
	
	// TRAINING DATA ====================================================================
	
	private final List<String> positiveReviewWords = new ArrayList<>() {
		private static final long serialVersionUID = 534854541168162469L;

	{
		addAll(Arrays.asList(SentimentTrainingData.POSITIVE.split(" ")));
	}};
	
	private final List<String> negativeReviewWords = new ArrayList<>() {
		private static final long serialVersionUID = 3285357761576797974L;
	{
		addAll(Arrays.asList(SentimentTrainingData.NEGATIVE.split(" ")));
	}};
	
	
	private final Set<String> allWords = new HashSet<>() {
		private static final long serialVersionUID = -6265406244044672035L;
	{
		addAll(positiveReviewWords);
		addAll(negativeReviewWords);
		
		removeAll(StopWords.all);
	}};
	
	
	// FREQUENCY BANKS ==================================================================
	
	private final Map<String, Integer> positiveWordFrequencyBank = new HashMap<>() {
		private static final long serialVersionUID = -8961571078050171718L;
	{
		allWords.forEach(word -> {
			put(word, 0);
		});
		
		positiveReviewWords.forEach(word -> {
			if (containsKey(word)) {
				put(word, get(word) + 1);
			}
		});
	}};
	
	private final Map<String, Integer> negativeWordFrequencyBank = new HashMap<>() {
		private static final long serialVersionUID = 532286387197190001L;
	{
		allWords.forEach(word -> {
			put(word, 0);
		});
		
		negativeReviewWords.forEach(word -> {
			if (containsKey(word)) {
				put(word, get(word) + 1);
			}
		});
	}};
	
	
	// PUBLIC INTERFACE =================================================================

	public void run() {
		if (GenrePreprocessor.lastRunId == lastRunId) {
			System.out.println("NOTICE: Not running sentiment analysis as sentiments are in-sync with genres.");
			return;
		}
		
		lastRunId = GenrePreprocessor.lastRunId;
		
		System.out.println("NOTICE: Analyzing review sentiments...");
		analyzeReviewSentiments();
		
		System.out.println("NOTICE: Analyzing user preferences...");
		analyzeUserPreferences();	
	}
	
	// ACTIONS ==========================================================================
	
	private void analyzeReviewSentiments() {
		List<EntryDocument> entries = dataCoordinator.getAllEntries();
		
		entries.parallelStream().forEach(entry -> {
			Optional<Boolean> sentimentIsPositive = sentimentIsPositiveForEntry(entry);
			
			if (sentimentIsPositive.isPresent()) {
				entry.setSentiment(sentimentIsPositive.get());
				dataCoordinator.upsert(entry);
			}
		});
	}
	
	private void analyzeUserPreferences() {
		List<UserDocument> users = dataCoordinator.getAllUsers();
		
		users.parallelStream().forEach(user -> {
			HashMap<String, Integer> scores = new HashMap<>() {
				private static final long serialVersionUID = -8099737721310345421L;
			{
				Genre.all.parallelStream().forEach(genre -> {
					put(genre, 0);
				});
			}};
			
			List<EntryDocument> entries = user.getEntries();
						
			entries.parallelStream().forEach(entry -> {
				Optional<Boolean> sentiment = entry.getSentiment();
				
				if (sentiment.isPresent() && sentiment.get() == true) {
					String genre1 = entry.getPage().getGenre();
					scores.put(genre1, scores.get(genre1) + 1);
					
					String genre2 = entry.getPage().getSecondaryGenre();
					scores.put(genre2, scores.get(genre2) + 1);
				}
			});
			
			String highestScoringGenre = GenrePreprocessor.getHighestScoringGenre(scores);
			
			if (scores.get(highestScoringGenre) != 0) {
				user.setCommunity(highestScoringGenre);
				dataCoordinator.upsert(user);
			}
		});
	}
	
	
	// PRIVATE HELPERS ==================================================================
	
	private Optional<Boolean> sentimentIsPositiveForEntry(EntryDocument entry) {
		Double positiveScore = 1.0;
		Double negativeScore = 1.0;
				
		for (String word : StringCleaner.stripExtraWhitespace(entry.getText()).split(" ")) {
			if (positiveWordFrequencyBank.containsKey(word)) {
				positiveScore *= scoreForWord(positiveWordFrequencyBank, word);
			}
			
			if (negativeWordFrequencyBank.containsKey(word)) {				
				negativeScore *= scoreForWord(negativeWordFrequencyBank, word);
			}
		}
		
		if (positiveScore == 1.0 && negativeScore == 1.0) {
			return Optional.ofNullable(null);
		}
		
		if (positiveScore > negativeScore) {
			return Optional.of(true);
		} else {
			return Optional.of(false);
		}
	}
	
	private Double scoreForWord(Map<String, Integer> bank, String word) {
		return (bank.get(word).doubleValue() + 1) / (allWords.size() + getSum(positiveWordFrequencyBank));
	}
	
	private static Integer getSum(final Map<String, Integer> data) { 
		Integer sum = 0; 
		for (String key: data.keySet()) sum += data.get(key);
		return sum; 
	}
}
