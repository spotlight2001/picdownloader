package at.wm.picloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class PicLoaderMain implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PicLoaderMain.class);

	@Autowired
	private CrawlerServiceImpl crawlerService;

	public void run(String... args) throws Exception {
		LOGGER.info("starting");
		PicLoaderArguments cfg = new PicLoaderArguments().setStartUrl(args[0])
				.setSingleImagePagePattern(args[1].toLowerCase())
				.setSingleIMagePattern(args[2].toLowerCase()).setType(args[3]);
		crawlerService.loadPictures(cfg, "f:/tmp/crawler4j");
		LOGGER.info("stopping");
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(PicLoaderMain.class, args);
	}
}