package at.wm.picloader;


public class PicLoaderArguments {
	private String startUrl;

	private String singleImagePagePattern;

	private String singleImagePattern;

	private String type;

	public String getStartUrl() {
		return startUrl;
	}

	public PicLoaderArguments setStartUrl(String startUrl) {
		this.startUrl = startUrl;
		return this;
	}

	public String getSingleImagePagePattern() {
		return singleImagePagePattern;
	}

	public PicLoaderArguments setSingleImagePagePattern(
			String singleImagePagePattern) {
		this.singleImagePagePattern = singleImagePagePattern;
		return this;
	}

	public String getSingleIMagePattern() {
		return singleImagePattern;
	}

	public PicLoaderArguments setSingleIMagePattern(String singleIMagePattern) {
		this.singleImagePattern = singleIMagePattern;
		return this;
	}

	public String getType() {
		return type;
	}

	public PicLoaderArguments setType(String type) {
		this.type = type;
		return this;
	}
}