package at.wm.picloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import at.wm.picloader.crawler.PicLoaderCrawler;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

@Component
public class CrawlerServiceImpl {

	@Autowired
	private ApplicationContext applicationContext;

	public void loadPictures(PicLoaderArguments cfg, String storageFolder)
			throws Exception {
		String crawlStorageFolder = "f:/tmp/crawler4j/test";
		int numberOfCrawlers = 1;

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setFollowRedirects(true);
		config.setMaxDepthOfCrawling(2);
		config.setConnectionTimeout(20000);
		config.setSocketTimeout(20000);
		config.setIncludeBinaryContentInCrawling(true);
		config.setUserAgentString("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
		config.setMaxDownloadSize(config.getMaxDownloadSize() * 10);

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher,
				robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		controller.addSeed(cfg.getStartUrl());

		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		PicLoaderCrawler.init(cfg, applicationContext);
		controller.start(PicLoaderCrawler.class, numberOfCrawlers);
	}
}