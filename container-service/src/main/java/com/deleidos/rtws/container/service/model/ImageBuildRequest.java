package com.deleidos.rtws.container.service.model;

import java.io.File;

public class ImageBuildRequest {

	private String bucket;
	private String accessKey;
	private String secretKey;
	private String domain;
	private String imageName; 
	private String softwareVersion;
	private File buildArea;
	private String ticket;

	public ImageBuildRequest withBucket(String bucket) {
		this.bucket = bucket;
		return this;
	}

	public ImageBuildRequest withAccessKey(String accessKey) {
		this.accessKey = accessKey;
		return this;
	}

	public ImageBuildRequest withSecretKey(String secretKey) {
		this.secretKey = secretKey;
		return this;
	}

	public ImageBuildRequest withDomain(String domain) {
		this.domain = domain;
		this.imageName = domain.split("\\.")[0];
		return this;
	}

	public ImageBuildRequest withSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
		return this;
	}

	public ImageBuildRequest withBuildArea(String buildArea) {
		this.buildArea = new File(buildArea);
		return this;
	}

	public ImageBuildRequest withTicket(String ticket) {
		this.ticket = ticket;
		return this;
	}

	public String getDomain() {
		return domain;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public String getTicket() {
		return ticket;
	}

	public File getBuildArea() {
		return buildArea;
	}

	@Override
	public String toString() {
		return String.format(
				"ImageBuildRequest [bucket=%s, accessKey=%s, secretKey=%s, domain=%s, imageName=%s, softwareVersion=%s, buildArea=%s, ticket=%s]",
				bucket, accessKey.substring(0, 5), secretKey.substring(0, 5), domain, imageName, softwareVersion, buildArea, ticket);
	}

	public String getBucket() {
		return bucket;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getImageName() {
		return imageName;
	}

}
