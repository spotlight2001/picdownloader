package at.wm.picloader.crawler;

import org.junit.Assert;
import org.junit.Test;

public class PicLoaderCrawlerTest {

	private PicLoaderCrawler testMe = new PicLoaderCrawler();

	@Test
	public void testGetNameFromUrl() {
		Assert.assertEquals("01.jpg",
				testMe.getNameFromUrl("http://www.mydomain.at/bla/01.jpg"));
	}
}