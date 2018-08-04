package com.deleidos.rtws.container.service.util;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.DockerClientBuilder;

public class DeleteImageUtil {

	private static AuthConfig authConfig = null;

	private static String registry = null;

	public static String userName = null;

	public static String pw = null;

	public static String email = null;

	public static Set<String> imagesToDelete = new CopyOnWriteArraySet<String>();

	public static Set<String> imagesToDeleteWithDelay = new CopyOnWriteArraySet<String>();

	private static Logger logger = LoggerFactory.getLogger(DeleteImageUtil.class);

	public static void queueForRemoval(String image) {
		logger.info("Queueing {} for removal", image);
		imagesToDelete.add(image);
	}

	public static void queueForDelayedRemoval(String image) {
		logger.info("Queueing {} for delayed removal", image);
		imagesToDeleteWithDelay.add(image);
	}

	public static synchronized void deleteImages() {

		DockerClient dockerClient = null;

		if (System.getenv("REGISTRY") != null) {
			logger.info(String.format("Assuming all params are provided for: %s", System.getenv("REGISTRY")));
			registry = System.getenv("REGISTRY");
			userName = System.getenv("U");
			pw = System.getenv("P");
			email = System.getenv("E");
		} else {
			logger.error("Credentials were not provided, not attempting to delete images");
			return;
		}

		if (authConfig == null)
			initilize();

		try {
			dockerClient = DockerClientBuilder.getInstance("unix:///run/docker.sock").build();
			logger.info("Docker client created: {} ", dockerClient.infoCmd().exec());

			for (String image : imagesToDelete) {
				logger.info("Removing id: {}", image);
				try {
					dockerClient.removeImageCmd(image).withForce(true).exec();
					imagesToDelete.remove(image);
				} catch (DockerException e) {
					logger.error(e.getMessage());
				}
			}

			/*
			 * for the base images, remove after N builds in order to optimize
			 * for repeat appliance creations 
			 */
			if (imagesToDeleteWithDelay.size() >= 3) {
				for (String image : imagesToDeleteWithDelay) {
					logger.info("Removing id: {}", image);
					try {
						dockerClient.removeImageCmd(image).withForce(true).exec();
						imagesToDeleteWithDelay.remove(image);
					} catch (DockerException e) {
						logger.error(e.getMessage());
					}
				}
			}
		} catch (DockerClientException e) {
			logger.error(e.getMessage());
		} finally {
			if (dockerClient != null) {
				try {
					dockerClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}

			if (imagesToDelete.size() != 0) {
				logger.warn("Failed to delete these images at this time.");
				for (String imageToDelete : imagesToDelete) {
					logger.info(imageToDelete);
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
