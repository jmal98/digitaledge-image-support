package com.deleidos.rtws.container.service.util;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.LoggerFactory;

import com.deleidos.rtws.container.service.model.ImageBuildRequest;

public enum BuildQueue {

	instance;

	private Deque<ImageBuildRequest> queue = new ConcurrentLinkedDeque<ImageBuildRequest>();
	private List<String> inProgress = new CopyOnWriteArrayList<String>();

	public ImageBuildRequest poll() {
		return queue.poll();
	}

	public void queueRequest(ImageBuildRequest request) {
		LoggerFactory.getLogger(getClass()).info("Queueing request: {}", request);
		queue.push(request);
	}
	
	public boolean isInProgress(String ticket) {
		return inProgress.contains(ticket);
	}

	public void markInProgress(String ticket) {
		inProgress.add(ticket);

	}
}
