package com.crunchify.restjersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/rest")
public class WebSemantica_rest {
	@GET
	@Produces("application/json")
	public String ping() {
		return "Ping_WebSemantica";
	}

}
