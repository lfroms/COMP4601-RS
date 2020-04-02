package edu.carleton.comp4601.utility;

public final class HTMLFrameGenerator {
	public static String wrapInHTMLFrame(String title, String htmlContent) {
		String output = "";
		
		output += "<html>";
		output += "<head>";
		output += "<title>";
		
		output += title;
		
		output += "</title>";
		output += "</head>";
		
		output += "<body>";
		
		output += htmlContent;
		
		output += "</body>";
		output += "</html>";
		
		return output;
	}
}
