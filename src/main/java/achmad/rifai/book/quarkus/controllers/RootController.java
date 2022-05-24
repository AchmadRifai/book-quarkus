package achmad.rifai.book.quarkus.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/**/*")
@Produces(MediaType.APPLICATION_JSON)
public class RootController {

	@POST
	public Response create() {
		throw new NotFoundException();
	}

	@GET
	public Response get() {
		throw new NotFoundException();
	}

}
