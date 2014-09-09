package at.wm.picloader;

import org.junit.Assert;
import org.junit.Test;

public class ImageStorageServiceImplTest {

	private ImageStorageServiceImpl testMe = new ImageStorageServiceImpl();

	@Test
	public void testGetHighestFolderNumber() {
		Assert.assertEquals(new Integer(6), testMe.getHighestFolderNumber(
				"src/test/resources/sampleStorage",
				"src/test/resources/sampleStorage2"));
	}

	@Test
	public void testExtractNumberFromString() {
		Assert.assertEquals(new Integer(123),
				testMe.extractNumberFromString("abc123"));
	}
}