package at.wm.picloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ImageStorageServiceImpl implements
		ApplicationListener<ImageDownloadEvent> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ImageStorageServiceImpl.class);

	public Integer getHighestFolderNumber(String... folders) {

		final FolderNumberContainer folderNumberContainer = new FolderNumberContainer();

		for (String folder : folders) {
			Path path = Paths.get(folder);
			try {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs) throws IOException {
						Path folderName = dir.getFileName();
						Integer folderNumber = extractNumberFromString(folderName
								.toString());
						if (folderNumber != null
								&& folderNumberContainer.number < folderNumber) {
							folderNumberContainer.number = folderNumber;
						}
						return super.preVisitDirectory(dir, attrs);
					}
				});
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return folderNumberContainer.number;
	}

	private class FolderNumberContainer {
		public Integer number = 0;
	}

	Integer extractNumberFromString(String text) {
		String numberStr = text.replaceAll("\\D", "");
		if (StringUtils.isEmpty(numberStr) || !StringUtils.isNumeric(numberStr)) {
			return -1;
		}
		return NumberUtils.createInteger(numberStr);
	}

	public File store(String filename, String type, byte[] data) {
		Assert.isTrue(data.length > 0);
		int highestFolderNumber = getHighestFolderNumber("f:/tmp/in/other/pik/");
		if (highestFolderNumber <= 0) {
			throw new IllegalArgumentException("highest folder number: "
					+ highestFolderNumber);
		}
		File baseFolder = new File("f:/tmp/in/other/pik/new/");
		Assert.isTrue(baseFolder.exists() && baseFolder.isDirectory()
				&& baseFolder.canWrite());
		File folder = new File(baseFolder, type + "-" + highestFolderNumber);
		if (!folder.exists()) {
			Assert.isTrue(folder.mkdir());
		}
		File file = new File(folder, filename);
		try {
			IOUtils.write(data, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LOGGER.debug("stored file to: " + file.getAbsolutePath());
		return file;
	}

	@Override
	public void onApplicationEvent(ImageDownloadEvent event) {
		store(event.getName(), event.getType(), event.getBinary());
	}
}