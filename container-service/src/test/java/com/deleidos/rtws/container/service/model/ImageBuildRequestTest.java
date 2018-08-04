package com.deleidos.rtws.container.service.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImageBuildRequestTest {

	@Test
	public void imageName() {
		ImageBuildRequest req = new ImageBuildRequest().withDomain("logsa.aws-interns.deleidos.com_base");
		assertEquals("logsa", req.getImageName());
	}
}
