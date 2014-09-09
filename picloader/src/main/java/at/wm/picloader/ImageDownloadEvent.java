package at.wm.picloader;

import org.springframework.context.ApplicationEvent;

public class ImageDownloadEvent extends ApplicationEvent {

	private static final long serialVersionUID = 75345064830194627L;

	private String url;

	private String name;

	private byte[] binary;

	private String type;

	public ImageDownloadEvent(Object source) {
		super(source);
	}

	public String getUrl() {
		return url;
	}

	public ImageDownloadEvent setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getName() {
		return name;
	}

	public ImageDownloadEvent setName(String name) {
		this.name = name;
		return this;
	}

	public byte[] getBinary() {
		return binary;
	}

	public ImageDownloadEvent setBinary(byte[] binary) {
		this.binary = binary;
		return this;
	}

	public String getType() {
		return type;
	}

	public ImageDownloadEvent setType(String type) {
		this.type = type;
		return this;
	}
}