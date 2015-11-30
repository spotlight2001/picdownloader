package at.wm.picloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
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

	// we assume singleton here
	private File folder;

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

	public File getFolder(String type) {
		int highestFolderNumber = getHighestFolderNumber("f:/tmp/in/other/pik/");
		if (highestFolderNumber <= 0) {
			throw new IllegalArgumentException("highest folder number: "
					+ highestFolderNumber);
		}
		File baseFolder = new File("f:/tmp/in/other/pik/new/");
		Assert.isTrue(baseFolder.exists() && baseFolder.isDirectory()
				&& baseFolder.canWrite());

		// pick first empty or non-existent folder
		while (true) {
			File folder = new File(baseFolder, type + "-" + highestFolderNumber);
			if (!folder.exists()) {
				Assert.isTrue(folder.mkdir());
			}
			Collection<File> listFiles = FileUtils.listFiles(folder, null,
					false);
			boolean isFolderEmpty = listFiles.size() <= 0;
			if (isFolderEmpty) {
				return folder;
			} else {
				highestFolderNumber++;
			}
		}
	}

	public File store(String filename, String type, byte[] data) {
		Assert.isTrue(data.length > 0);

		if (folder == null) {
			this.folder = getFolder(type);
		}

		File file = new File(folder, filename);

		// if file already exist - only overwrite with larger version
		// e.g.: thumbnail vs. real img
		if (file.exists()) {
			try {
				byte[] dataOfExistingFile = IOUtils
						.toByteArray(new FileInputStream(file));
				if (data.length <= dataOfExistingFile.length) {
					LOGGER.info("dont save file from url, because existing file with same name is equal or larger");
					return file;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		FileOutputStream fis = null;
		try {
			fis = new FileOutputStream(file);
			IOUtils.write(data, new FileOutputStream(file));
			fis.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Assert.isTrue(file.exists() && file.isFile());
		LOGGER.info("stored file to: " + file.getAbsolutePath());
		return file;
	}

	@Override
	public void onApplicationEvent(ImageDownloadEvent event) {
		store(event.getName(), event.getType(), event.getBinary());
	}
}