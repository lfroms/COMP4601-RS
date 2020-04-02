package edu.carleton.comp4601.crawler;

import java.io.File;

import edu.carleton.comp4601.store.DataCoordinator;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

final class CrawlerController {
	private static final int NUM_CRAWLERS = 7;
	private static final String STORAGE_PATH = new File(System.getProperty("user.home"), "/Desktop/crawler.nosync")
			.toString();
	
	private static DataCoordinator dataCoordinator = DataCoordinator.getInstance();

	public static void main(String[] args) throws Exception {
		dataCoordinator.reset();
		
		CrawlConfig config = getCrawlConfig();

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		controller.addSeed("https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/users");
		controller.addSeed("https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages");

		CrawlController.WebCrawlerFactory<Crawler> factory = Crawler::new;
		controller.start(factory, NUM_CRAWLERS);

		System.out.println("NOTICE: Running preprocessor for GENRES...");
		dataCoordinator.preprocess();
	}

	private static CrawlConfig getCrawlConfig() {
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(STORAGE_PATH);
		config.setPolitenessDelay(10);
		config.setIncludeHttpsPages(true);
		config.setIncludeBinaryContentInCrawling(false);
		config.setMaxDownloadSize(1_000_000_000);
		config.setResumableCrawling(false);

		config.setMaxPagesToFetch(100_000);

		return config;
	}
}
