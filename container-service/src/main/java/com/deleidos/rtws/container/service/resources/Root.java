package com.deleidos.rtws.container.service.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Silent ignore for requests to /
 *
 */
@Path("/")
public class Root {

	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "";
	}
}
