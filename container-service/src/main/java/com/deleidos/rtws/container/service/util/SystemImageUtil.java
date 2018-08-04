package com.deleidos.rtws.container.service.util;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PushImageResultCallback;

public class SystemImageUtil {

	private static AuthConfig authConfig = null;

	private static String registry = null;

	public static String userName = null;

	public static String pw = null;

	public static String email = null;

	public static synchronized void tagAndPush(String image) {

		Logger logger = LoggerFactory.getLogger(SystemImageUtil.class);
		DockerClient dockerClient = null;

		if (System.getenv("REGISTRY") != null) {
			logger.info(String.format("Assuming all params are provided for: %s", System.getenv("REGISTRY")));
			registry = System.getenv("REGISTRY");
			userName = System.getenv("U");
			pw = System.getenv("P");
			email = System.getenv("E");
		} else {
			logger.error("Credentials were not provided, not pushing image");
			return ;
		}

		if (authConfig == null)
			initilize();

		boolean pushSuccessful = false;
		int retries = 0;
		while (!pushSuccessful) {

			if (retries >= 5) {
				logger.warn(String.format("Retries exceeded attempting to push %s/%s", registry, image.split(":")[0]));
				return;
			}

			try {
				dockerClient = DockerClientBuilder.getInstance("unix:///run/docker.sock").build();
				logger.info("Docker client created: {} ", dockerClient.infoCmd().exec());

				if (image != null) {
					logger.info("Tagging image: {}", image);
					dockerClient.tagImageCmd(image, String.format("%s/%s", registry, image.split(":")[0]),
							image.split(":")[1]).withForce(true).exec();

					String taggedImage = String.format("%s/%s", registry, image);
					logger.info("Pushing image: {} which may take awhile...", taggedImage);

					LocalDateTime startTime = LocalDateTime.now();

					dockerClient.pushImageCmd(String.format("%s/%s", registry, image.split(":")[0]))
							.withTag(image.split(":")[1]).withAuthConfig(authConfig).exec(new PushImageResultCallback())
							.awaitSuccess();
					LocalDateTime endTime = LocalDateTime.now();

					long diffInMinutes = Duration.between(startTime, endTime).toMinutes();
					logger.info("Completed pushing image: {} completed in {} minutes.", taggedImage, diffInMinutes);
					pushSuccessful = true;
					DeleteImageUtil.queueForRemoval(String.format("%s/%s:%s", registry, image.split(":")[0],image.split(":")[1]));
					DeleteImageUtil.queueForRemoval(image);
				}
			} catch (DockerClientException e) {
				logger.error(e.getMessage());
				retries++;

				logger.info("Retrying...");
				try {
					Thread.sleep(TimeUnit.MINUTES.toMillis(2l));
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage());
				}

			} finally {
				if (dockerClient != null) {
					try {
						dockerClient.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	private static synchronized void initilize() {
		authConfig = new AuthConfig();
		authConfig.withUsername(userName);
		authConfig.withPassword(pw);
		authConfig.withEmail(email);
		authConfig.withRegistryAddress(registry);
	}

}
