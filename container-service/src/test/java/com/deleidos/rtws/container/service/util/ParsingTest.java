package com.deleidos.rtws.container.service.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.Test;

public class ParsingTest {

	@Test
	public void parse() throws IOException {
		String df = read(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("dockerfiles/Dockerfile_centos_system_transport"));
		assertNotNull(df);
		System.out.println(df.replace("SYSTEM_RELEASE_AND_TAG_PLACEHOLDER", "rtws-nightly.2222"));
		assertTrue(df.contains("SYSTEM_RELEASE_AND_TAG_PLACEHOLDER"));
		
		
	}

	public static String read(InputStream input) throws IOException {
		String content = null;
		Scanner scanner = new Scanner(input, "utf-8");

		content = scanner.useDelimiter("\\Z").next();
		scanner.close();
		input.close();
		return content;

	}

}
