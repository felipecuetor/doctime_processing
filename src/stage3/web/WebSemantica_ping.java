package stage3.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

//To test:
//http://localhost:8080/WebSemantica_Proyecto/webapp/ping/
@Path("/ping")
public class WebSemantica_ping {
	@GET
	@Produces("application/json")
	public String ping() {
		return "{\"ping\":\"Ping_WebSemantica\"}";
	}
}
