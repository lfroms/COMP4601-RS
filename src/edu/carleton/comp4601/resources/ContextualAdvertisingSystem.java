package edu.carleton.comp4601.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.carleton.comp4601.analyzers.SentimentPostprocessor;
import edu.carleton.comp4601.crawler.CrawlerController;
import edu.carleton.comp4601.models.PageDocument;
import edu.carleton.comp4601.models.UserDocument;
import edu.carleton.comp4601.store.DataCoordinator;
import edu.carleton.comp4601.utility.ContentAugmenter;
import edu.carleton.comp4601.utility.HTMLFrameGenerator;

@Path("/rs")
public class ContextualAdvertisingSystem {
	private static final String NAME = "Lukas Romsicki & Britta Evans-Fenton";
	
	private static final DataCoordinator dataCoordinator = DataCoordinator.getInstance();
	
	@GET
	public String nameOfSystem() {
		return NAME;
	}
	
	@Path("crawl")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String crawlProcessor() {
		CrawlerController controller = new CrawlerController();
		try {
			controller.crawlData();
		} catch (Exception e) {
			System.err.println("ERROR: An error occurred while crawling the data.");
			e.printStackTrace();
			
			return "An error occurred while crawling. See console stacktrace.";
		}
		
		return "Crawl completed.";
	}
	
	@Path("context")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String contextProcessor() {
		SentimentPostprocessor sentimentPostprocessor = new SentimentPostprocessor();
		sentimentPostprocessor.run();
		
		String output = "";
		output += "<table>";
		output += "<tr>";
		output += "<th>Name</th><th># of reviews</th>";
		output += "</tr>";
		
		for (UserDocument user : dataCoordinator.getAllUsers()) {
			output += "<tr>";
			output += "<td>";
			output += user.getId();
			output += "</td>";
			output += "<td>";
			output += user.getPageIds().toArray().length;
			output += "</td>";
			output += "</tr>";
		}
		
		output += "</table>";
		
		return HTMLFrameGenerator.wrapInHTMLFrame("Context", output);
	}
	
	@Path("community")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String communityProcessor() {
		return "Return a table as per requirement 8 of assignment 2";
	}
	
	@Path("fetch/{user}/{page}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String fetchUserPageProcessor(@PathParam("user") String user, @PathParam("page") String page) {
		return ContentAugmenter.wrap(user, page);
	}
	
	@Path("advertising/{category}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String advertisingCategoryProcessor(@PathParam("category") String category) {
		// TODO: - Get proper pages.
		List<PageDocument> pagesToAdvertise = dataCoordinator.getAllPages().subList(0, 2);
		
		String output = "";
		output += "<h2>You may also like:</h2>";
		output += "<table>";
		output += "<tr>";
		output += "<th>Page Name</th><th># of reviews</th>";
		output += "</tr>";
		
		for (PageDocument page : pagesToAdvertise) {
			output += "<tr>";
			output += "<td>";
			output += "<a href=\"" + page.getURL().getURL() + "\" target='_blank'>";
			output += page.getId();
			output += "</a>";
			output += "</td>";
			output += "<td>";
			output += page.getUserIds().toArray().length;
			output += "</td>";
			output += "</tr>";
		}
		
		output += "</table>";
		
		return HTMLFrameGenerator.wrapInHTMLFrame("Advertising for " + category, output);
	}
}
