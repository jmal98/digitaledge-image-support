package com.deleidos.rtws.container.service.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryZipProcessingUtilTest {

	private static File repoZipForDeletion = null;
	private Logger logger = LoggerFactory.getLogger(RepositoryZipProcessingUtilTest.class);

	@BeforeClass
	public static void init() throws IOException {
		repoZipForDeletion = File.createTempFile("repo_", ".zip");
		Files.copy(FileSystems.getDefault().getPath("./src/test/resources/repo.zip"),
				FileSystems.getDefault().getPath(repoZipForDeletion.getAbsolutePath()),
				StandardCopyOption.REPLACE_EXISTING);
	}

	@AfterClass
	public static void cleanup() throws IOException {
		Files.delete(FileSystems.getDefault().getPath(repoZipForDeletion.getAbsolutePath()));
	}

	@Test
	public void deleteFromZip() {
		RepositoryZipProcessingUtil.deleteFromZip(repoZipForDeletion, "private/datasinks/commons-io-2.4.jar",
				"private/datasinks/doesnotexist.jar", "commons-lang-2.5.jar");

		Map<String, String> props = new HashMap<>();
		props.put("create", "false");

		URI zipUri = URI.create(String.format("jar:file:%s", repoZipForDeletion.getAbsolutePath()));

		try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, props)) {

			assertFalse(Files.exists(zipfs.getPath("private/datasinks/commons-io-2.4.jar")));
			assertTrue(Files.exists(zipfs.getPath("commons/commons-httpclient-3.1.jar")));
			assertFalse(Files.exists(zipfs.getPath("commons-lang-2.5.jar")));

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, props)) {

			Stream<Path> stream = Files.walk(zipfs.getPath("/"), FileVisitOption.FOLLOW_LINKS);
			Iterator<Path> iterator = stream.iterator();
			while (iterator.hasNext())
				logger.info(((Path) iterator.next()).toString());

			stream.close();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void containsS2iArtifacts() {

		Map<String, String> props = new HashMap<>();
		props.put("create", "false");

		URI zipUri = URI.create(String.format("jar:file:%s", repoZipForDeletion.getAbsolutePath()));

		logger.info("Archive before deletion of s2i artifacts.");
		try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, props)) {

			Stream<Path> stream = Files.walk(zipfs.getPath("/"), FileVisitOption.FOLLOW_LINKS);
			Iterator<Path> iterator = stream.iterator();
			while (iterator.hasNext())
				logger.info(((Path) iterator.next()).toString());

			stream.close();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		List<String> s2iArtfcts = RepositoryZipProcessingUtil.containsS2iArtifacts(repoZipForDeletion);
		assertEquals(2, s2iArtfcts.size());
		for (String s2iart : s2iArtfcts) {
			logger.info("s2i artifact: {}", s2iart);

		}

		RepositoryZipProcessingUtil.deleteFromZip(repoZipForDeletion, s2iArtfcts);

		logger.info("Archive after deletion of s2i artifacts.");

		try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, props)) {

			assertFalse(Files.exists(zipfs.getPath("private/datasinks/deleidos-rtws-plugin-datasink-s2i-hdfs.jar")));
			assertTrue(Files.exists(zipfs.getPath("commons/commons-httpclient-3.1.jar")));
			assertFalse(Files
					.exists(zipfs.getPath("private/datasinks/deleidos-rtws-plugin-datasink-s2i-hdfs-plugin-deps.zip")));

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, props)) {

			Stream<Path> stream = Files.walk(zipfs.getPath("/"), FileVisitOption.FOLLOW_LINKS);
			Iterator<Path> iterator = stream.iterator();
			while (iterator.hasNext())
				logger.info(((Path) iterator.next()).toString());

			stream.close();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

}