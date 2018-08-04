package com.deleidos.rtws.container.service.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deleidos.rtws.container.service.model.ImageBuildRequest;
import com.deleidos.rtws.container.service.model.ImageBuildResult;
import com.deleidos.rtws.container.service.model.PlaybookParams;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

public class SystemBaseImageCallable implements Callable<ImageBuildResult> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private ImageBuildRequest request = null;
	private DockerClient dockerClient = null;

	@SuppressWarnings("unused")
	private static class SystemPlaybookParams extends PlaybookParams {

		private String rtws_release_bucket;
		private String aws_access_key;
		private String aws_secret_key;

		public SystemPlaybookParams(String rtws_release_bucket, String rtws_release, String rtws_system,
				String aws_access_key, String aws_secret_key) {
			super(rtws_release, rtws_system);

			this.rtws_release_bucket = rtws_release_bucket;
			this.aws_access_key = aws_access_key;
			this.aws_secret_key = aws_secret_key;
		}

		public String getRtws_release_bucket() {
			return rtws_release_bucket;
		}

		public String getAws_access_key() {
			return aws_access_key;
		}

		public String getAws_secret_key() {
			return aws_secret_key;
		}
	}

	public SystemBaseImageCallable(ImageBuildRequest buildRequest) {
		this.request = buildRequest;
	}

	@Override
	public ImageBuildResult call() throws Exception {
		ImageBuildResult result = new ImageBuildResult();

		try {
			logger.info("Starting image build for: {}", request);

			File buildTarget = setupBuildTarget();

			dockerClient = DockerClientBuilder.getInstance("unix:///run/docker.sock").build();
			logger.info("client created.  " + dockerClient.infoCmd().exec());

			DockerImageBuildCallback callback = new DockerImageBuildCallback();
			String tag = String.format("%s/%s_base:%s", "digitaledge", request.getDomain(),
					request.getSoftwareVersion());

			logger.info("Build area: {} for image: {}", buildTarget.getAbsolutePath(), tag);
			LocalDateTime startTime = LocalDateTime.now();
			
			String image = dockerClient.buildImageCmd(buildTarget).withTag(tag).withRemove(true).withForcerm(true)
					.exec(callback).awaitImageId();
			
			LocalDateTime endTime = LocalDateTime.now();
			long diffInMinutes = Duration.between(startTime, endTime).toMinutes();

			logger.info("Image build completed for image: {} completed in {} minutes.", image, diffInMinutes);

			result.put("image", tag);
			result.put("domain", request.getDomain());
			result.put("result", true);
			
			DeleteImageUtil.queueForDelayedRemoval(tag);
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

	private File setupBuildTarget() {
		File target = new File(String.format("%s/%s", request.getBuildArea().getAbsolutePath(), "base"));
		logger.info("Target: {}", target.getAbsolutePath());
		if (!target.exists())
			target.mkdir();

		// Copy the Dockerfile
		InputStream dockerfileIs = null;
		try {
			dockerfileIs = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("dockerfiles/Dockerfile_centos_system_base");
			Files.copy(dockerfileIs,
					FileSystems.getDefault().getPath(target.getAbsolutePath() + File.separatorChar + "Dockerfile"));

			// Copy the playbook
			Path playbook = FileSystems.getDefault().getPath("/tmp/system-base.tar.gz");
			Files.copy(playbook, FileSystems.getDefault()
					.getPath(target.getAbsolutePath() + File.separatorChar + "system-base.tar.gz"));

			// Write the params
			SystemPlaybookParams params = new SystemPlaybookParams(this.request.getBucket(),
					this.request.getSoftwareVersion(), this.request.getDomain(), this.request.getAccessKey(),
					this.request.getSecretKey());
			YamlMapperCache.mapper.writeValue(new File(target.getAbsolutePath() + File.separatorChar + "params.yml"),
					params);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (dockerfileIs != null)
				try {
					dockerfileIs.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
		}

		return target;
	}

}
