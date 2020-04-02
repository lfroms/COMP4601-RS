package edu.carleton.comp4601.crawler;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import edu.carleton.comp4601.models.PageDocument;
import edu.carleton.comp4601.models.UserDocument;
import edu.carleton.comp4601.store.DataCoordinator;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

final class Crawler extends WebCrawler {
	private final static DataCoordinator dataCoordinator = DataCoordinator.getInstance();

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp4|zip|gz))$");

	private final String[] supportedUrls = { "https://sikaman.dyndns.org" };

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();

		return !FILTERS.matcher(href).matches() && stringStartsWithSupportedPrefix(href);
	}

	@Override
	public void visit(Page page) {
		String path = page.getWebURL().getPath();
		String name = path.substring(path.lastIndexOf('/') + 1);
		
		if (isUser(page)) {
			System.out.println("NOTICE: Saving user \"" + name + "\"");
			handleUser(page);

		} else if (isPage(page)) {
			System.out.println("NOTICE: Saving page \"" + name + "\"");
			handlePage(page);

		} else {
			System.err.println("WARNING: Could not classify \"" + name + "\". Skipping...");
		}
	}

	// PRIVATE HELPERS ==================================================================

	private void handlePage(Page page) {
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

		if (htmlParseData instanceof HtmlParseData == false) {
			return;
		}
		
		WebURL webUrl = page.getWebURL();
		PageDocument pageDocument = new PageDocument(webUrl, htmlParseData.getHtml());
		
		dataCoordinator.upsert(pageDocument);
		
		pageDocument.getEntries(Jsoup.parse(htmlParseData.getHtml())).forEach(entry -> {
			dataCoordinator.upsert(entry);
		});
	}
	
	private void handleUser(Page page) {
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

		if (htmlParseData instanceof HtmlParseData == false) {
			return;
		}
		
		WebURL webUrl = page.getWebURL();
		UserDocument pageDocument = new UserDocument(webUrl, htmlParseData.getHtml());
		
		dataCoordinator.upsert(pageDocument);
	}
	
	private Boolean isUser(Page page) {
		return page.getWebURL().getURL().contains("users") && page.getWebURL().getURL().contains(".html");
	}
	
	private Boolean isPage(Page page) {
		return page.getWebURL().getURL().contains("pages") && page.getWebURL().getURL().contains(".html");
	}
	
	private Boolean stringStartsWithSupportedPrefix(String input) {
		boolean result = false;

		for (int i = 0; i < supportedUrls.length; i++) {
			if (input.startsWith(supportedUrls[i])) {
				result = true;
				break;
			}
		}

		return result;
	}
}
