package edu.carleton.comp4601.analyzers.utility;

public final class StringCleaner {
	public static String stripExtraWhitespace(String input) {
		return input.trim().replaceAll(" +", " ");
	}
}
