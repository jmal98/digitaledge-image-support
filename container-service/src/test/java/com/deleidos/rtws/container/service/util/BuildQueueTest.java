package com.deleidos.rtws.container.service.util;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.deleidos.rtws.container.service.model.ImageBuildRequest;

public class BuildQueueTest {

	@Test
	public void queueTask() {

		BuildQueue.instance.queueRequest(
				new ImageBuildRequest().withBucket("mybucket").withAccessKey("abcd").withSecretKey("efg2/2")
						.withDomain("acme.aws-interns.deleidos.com").withSoftwareVersion("rtws-nightly:12323")
						.withBuildArea("/tmp/doesNotexist").withTicket(UUID.randomUUID().toString()));

		ImageBuildRequest queuedRequest = BuildQueue.instance.poll();
		assertNotNull(queuedRequest);
		assertEquals("rtws-nightly:12323", queuedRequest.getSoftwareVersion());
	}

	@Test
	public void inProgress() {
		BuildQueue.instance.queueRequest(
				new ImageBuildRequest().withBucket("mybucket").withAccessKey("abcd").withSecretKey("efg2/2")
						.withDomain("phantom.aws-interns.deleidos.com").withSoftwareVersion("rtws-nightly:98407809")
						.withBuildArea("/tmp/doesexist").withTicket(UUID.randomUUID().toString()));

		ImageBuildRequest queuedRequest = BuildQueue.instance.poll();
		BuildQueue.instance.markInProgress(queuedRequest.getTicket());

	}

}
