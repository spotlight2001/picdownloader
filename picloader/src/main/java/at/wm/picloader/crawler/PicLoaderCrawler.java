package at.wm.picloader.crawler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import at.wm.picloader.ImageDownloadEvent;
import at.wm.picloader.PicLoaderArguments;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class PicLoaderCrawler extends WebCrawler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PicLoaderCrawler.class);

	private static PicLoaderArguments cfg;

	private static ApplicationContext applicationContext;

	public static void init(PicLoaderArguments cfg,
			ApplicationContext applicationContext) {
		PicLoaderCrawler.cfg = cfg;
		PicLoaderCrawler.applicationContext = applicationContext;
	}

	private boolean isAllowedImage(String url) {
		url = url.toLowerCase();
		boolean result = url.contains(cfg.getSingleIMagePattern())
				&& endsWith(url, new String[] { "jpg", "jpeg" });
		return result;
	}

	private boolean isAllowedSingleImagePage(String url) {
		url = url.toLowerCase();
		url = StringUtils.split(url, "?")[0];
		boolean result = url.contains(cfg.getSingleImagePagePattern())
				&& endsWith(url, new String[] { "html", "htm", "/", "php" });
		return result;
	}

	private boolean endsWith(String subject, String... suffixes) {
		for (String suffix : suffixes) {
			if (subject.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	private boolean isSmallSizeImage(int numberOfBytes) {
		return (numberOfBytes / 1024) < 30;
	}
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		boolean allow = isAllowedImage(href) || isAllowedSingleImagePage(href);
		LOGGER.debug("follow url '{}'?: '{}'", href, allow);
		return allow;
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		LOGGER.debug("visited: {}", url);

		boolean isAllowedImage = isAllowedImage(url);

		if (!(isAllowedImage && page.getParseData() instanceof BinaryParseData)) {
			return;
		}

		byte[] binary = page.getContentData();

		if (isSmallSizeImage(binary.length)) {
			return;
		}

		LOGGER.info("download image: {}, size: {} kb", url,
				binary.length / 1024);

		String name = getNameFromUrl(url);
		applicationContext.publishEvent(new ImageDownloadEvent(url).setUrl(url)
				.setName(name).setBinary(binary).setType(cfg.getType()));
	}

	String getNameFromUrl(String url) {
		int indexOfLastSlash = url.lastIndexOf("/");
		String name = url.substring(indexOfLastSlash + 1);
		return name;
	}
}