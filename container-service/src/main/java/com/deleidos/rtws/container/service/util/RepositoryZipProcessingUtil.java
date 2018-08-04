package com.deleidos.rtws.container.service.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryZipProcessingUtil {

	public static void deleteFromZip(File zip, String... pathsToDelete) {
		Logger logger = LoggerFactory.getLogger(RepositoryZipProcessingUtil.class);

		Map<String, String> props = new HashMap<>();
		props.put("create", "false");

		URI zipUri = URI.create(String.format("jar:file:%s", zip.getAbsolutePath()));

		try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, props)) {

			for (String deleteme : pathsToDelete) {

				Path pathInZipFile = zipfs.getPath(deleteme);

				if (Files.exists(pathInZipFile)) {
					logger.info("Deleting S2i artifacts as they are not used in all appliances.");
					logger.info("About to delete an entry: {} from zip: {}", pathInZipFile.toUri(), zip);

					Files.delete(pathInZipFile);
					logger.info("Successfully deleted entry: {} from zip: {}", pathInZipFile.toUri(), zip);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static List<String> containsS2iArtifacts(File zip) {
		Logger logger = LoggerFactory.getLogger(RepositoryZipProcessingUtil.class);
		List<String> s2iArtifacts = new ArrayList<String>();

		Map<String, String> props = new HashMap<>();
		props.put("create", "false");

		URI zipUri = URI.create(String.format("jar:file:%s", zip.getAbsolutePath()));

		try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, props)) {

			Stream<Path> stream = Files.walk(zipfs.getPath("/"), FileVisitOption.FOLLOW_LINKS);
			Iterator<Path> iterator = stream.iterator();
			while (iterator.hasNext()) {
				Path pth = iterator.next();

				if (logger.isDebugEnabled())
					logger.debug(pth.toString());

				if (pth.getFileName() != null) {
					if (pth.getFileName().endsWith("deleidos-rtws-plugin-datasink-s2i-hdfs.jar")
							|| pth.getFileName().endsWith("deleidos-rtws-plugin-datasink-s2i-hdfs-plugin-deps.zip"))
						s2iArtifacts.add(pth.toString());
				}
			}

			stream.close();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return s2iArtifacts;

	}

	public static void deleteFromZip(File file, List<String> list) {
		deleteFromZip(file, list.stream().toArray(String[]::new));
	}
}
