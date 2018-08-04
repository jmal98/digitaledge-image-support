package com.deleidos.rtws.container.service.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerfileUtil {

	private synchronized static String read(InputStream input) throws IOException {
		String content = null;
		Scanner scanner = new Scanner(input, "utf-8");

		content = scanner.useDelimiter("\\Z").next();
		scanner.close();
		input.close();
		return content;

	}

	public static void updateFromDeclaration(String domain, String softwareVersion, InputStream source,
			File dockerfile) {
		Logger logger = LoggerFactory.getLogger(DockerfileUtil.class);
		Writer writer = null;

		try {
			String tmp = read(source);
			String dfContent = tmp.replace("SYSTEM_RELEASE_AND_TAG_PLACEHOLDER",
					String.format("%s_base:%s", domain, softwareVersion));
			writer = new FileWriter(dockerfile);
			writer.write(dfContent);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
		}

	}

}
