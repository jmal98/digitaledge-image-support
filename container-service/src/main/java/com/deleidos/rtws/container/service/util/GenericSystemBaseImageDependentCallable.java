package com.deleidos.rtws.container.service.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deleidos.rtws.container.service.model.ImageBuildRequest;
import com.deleidos.rtws.container.service.model.ImageBuildResult;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

public class GenericSystemBaseImageDependentCallable implements Callable<ImageBuildResult> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private ImageBuildRequest request = null;
	private DockerClient dockerClient = null;
	private String tag = null;
	private BuildAreaSetupUtil setupUtil;

	public GenericSystemBaseImageDependentCallable(ImageBuildRequest buildRequest, BuildAreaSetupUtil util,
			String tag) {
		this.request = buildRequest;
		this.setupUtil = util;
		this.tag = tag;
	}

	@Override
	public ImageBuildResult call() throws Exception {
		ImageBuildResult result = new ImageBuildResult();

		try {
			logger.info("Starting image build for: {}", request);

			File buildTarget = setupUtil.setupBuildTarget(request);

			dockerClient = DockerClientBuilder.getInstance("unix:///run/docker.sock").build();
			logger.info("Docker client created: {} ", dockerClient.infoCmd().exec());

			logger.info("Build area: {} for image: {}", buildTarget.getAbsolutePath(), tag);

			LocalDateTime startTime = LocalDateTime.now();

			dockerClient.buildImageCmd(buildTarget).withTag(tag).withRemove(true).withForcerm(true)
					.exec(new DockerImageBuildCallback()).awaitImageId();

			LocalDateTime endTime = LocalDateTime.now();

			long diffInMinutes = Duration.between(startTime, endTime).toMinutes();

			logger.info("Image build completed for image: {} completed in {} minutes.", tag, diffInMinutes);

			SystemImageUtil.tagAndPush(tag);

			result.put("domain", request.getDomain());
			result.put("result", true);

			// Cleanup work area on disk
			Files.walkFileTree(FileSystems.getDefault().getPath(buildTarget.getAbsolutePath()),
					new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							logger.info("Deleting: {}", file);
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
							logger.info("Deleting: {}", dir);
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						}

					});
		} finally {
			if (dockerClient != null) {
				try {
					dockerClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return result;
	}
}
