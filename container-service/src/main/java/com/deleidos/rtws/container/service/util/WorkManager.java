package com.deleidos.rtws.container.service.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deleidos.rtws.container.service.model.ImageBuildRequest;
import com.deleidos.rtws.container.service.model.ImageBuildResult;

public class WorkManager extends Thread {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private Logger logger = LoggerFactory.getLogger(getClass());

	private void doWork() {
		logger.info("Waiting for work.....");

		while (!Boolean.getBoolean("SHUTDOWN_REQUESTED")) {
			ImageBuildRequest buildRequest = null;
			try {
				Thread.sleep(TimeUnit.MINUTES.toMillis(1l));
				buildRequest = BuildQueue.instance.poll();
				if (buildRequest != null) {
					BuildQueue.instance.markInProgress(buildRequest.getTicket());
					ArrayList<ScheduledFuture<ImageBuildResult>> pending = new ArrayList<ScheduledFuture<ImageBuildResult>>();

					pending.add(scheduler.schedule(new SystemBaseImageCallable(buildRequest), 30l, TimeUnit.SECONDS));
					pending.add(scheduler.schedule(new GatewayImageCallable(buildRequest), 30l, TimeUnit.SECONDS));
					pending.add(scheduler.schedule(new ActivemqImageCallable(buildRequest), 30l, TimeUnit.SECONDS));
					pending.add(scheduler.schedule(new IngestImageCallable(buildRequest), 30l, TimeUnit.SECONDS));
					pending.add(scheduler.schedule(new CustomImageCallable(buildRequest), 30l, TimeUnit.SECONDS));
					pending.add(scheduler.schedule(new TransportImageCallable(buildRequest), 30l, TimeUnit.SECONDS));

					for (ScheduledFuture<ImageBuildResult> future : pending) {
						try {
							logger.info("" + future.isDone());
							ImageBuildResult result = future.get();
							logger.info(result.toString());
						} catch (ExecutionException e) {
							logger.error(e.getMessage(), e);
						} finally {
						}
					}
				}
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (buildRequest != null) {
					// Cleanup work area on disk
					try {
						Files.walkFileTree(
								FileSystems.getDefault().getPath(buildRequest.getBuildArea().getAbsolutePath()),
								new SimpleFileVisitor<Path>() {
									@Override
									public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
											throws IOException {
										logger.info("Deleting: {}", file);
										Files.delete(file);
										return FileVisitResult.CONTINUE;
									}

									@Override
									public FileVisitResult postVisitDirectory(Path dir, IOException exc)
											throws IOException {
										logger.info("Deleting: {}", dir);
										Files.delete(dir);
										return FileVisitResult.CONTINUE;
									}

								});
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
					DeleteImageUtil.deleteImages();
				}
			}
		}
	}

	@Override
	public void run() {
		doWork();
	}
}
