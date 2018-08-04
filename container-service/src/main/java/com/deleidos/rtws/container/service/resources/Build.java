package com.deleidos.rtws.container.service.resources;

import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deleidos.rtws.container.service.model.ImageBuildRequest;
import com.deleidos.rtws.container.service.util.BuildQueue;

@Path("/build")
public class Build {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@GET
	@Path("/status/{build_request}")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response build(@PathParam(value = "build_request") String ticket) {
		if (ticket == null)
			return Response.ok("false").build();

		try {
			return Response.ok(BuildQueue.instance.isInProgress(ticket)).build();
		} finally {

		}
	}

	@POST
	@Path("/{system_domain}/{software_version}")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response build(@PathParam(value = "system_domain") String domain,
			@PathParam(value = "software_version") String softwareVersion, @FormParam(value = "bucket") String bucket,
			@FormParam(value = "access_key") String accessKey, @FormParam(value = "secret_key") String secretKey,
			@FormParam(value = "build_directory") String buildDirectory) {

		try {
			logger.info("Received request: {} {} {}", domain, softwareVersion, buildDirectory);
			// Queue request
			String ticket = UUID.randomUUID().toString();
			BuildQueue.instance.queueRequest(
					new ImageBuildRequest().withBucket(bucket).withAccessKey(accessKey).withSecretKey(secretKey)
							.withDomain(domain).withSoftwareVersion(softwareVersion).withBuildArea(buildDirectory)
							.withTicket(ticket));

			return Response.ok(ticket).build();
		} finally {

		}
	}

}
