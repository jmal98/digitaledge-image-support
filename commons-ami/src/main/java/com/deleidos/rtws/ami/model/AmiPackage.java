package com.deleidos.rtws.ami.model;

import java.io.File;

public class AmiPackage {

	private File image;

	public void cleanup() {
		if (image.exists()) {

			if (!image.delete())
				image.deleteOnExit();
		}
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AmiPackage [image=").append(image).append("]");
		return builder.toString();
	}

}
