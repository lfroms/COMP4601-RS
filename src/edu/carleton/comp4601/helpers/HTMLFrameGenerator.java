package edu.carleton.comp4601.helpers;

public final class HTMLFrameGenerator {
	public static String wrapInHTMLFrame(String title, String htmlContent) {
		String output = "";
		
		output += "<html>";
		output += "<head>";
		output += "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://www.w3schools.com/w3css/4/w3.css\" />";
		output += "<title>";
		
		output += title;
		
		output += "</title>";
		output += "</head>";
		
		output += "<body class=\"w3-container\">";
		
		output += htmlContent;
		
		output += "</body>";
		output += "</html>";
		
		return output;
	}
}
