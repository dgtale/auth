package com.dg.sample.rest;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dg.sample.annotation.Secured;
import com.dg.sample.entity.user.Role;

@Path("/dummy")
@RequestScoped
public class DummyRest {

	@GET
	@Path("/unsecure")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getA() {
		Map<String, String> responseObj = new HashMap<>();
		responseObj.put("value", "GET Response of unsecured resource");
		Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseObj);
		return builder.build();
	}

	@GET
	@Path("/secure")
	@Secured()
	@Produces(MediaType.APPLICATION_JSON)
	public Response getB() {
		Map<String, String> responseObj = new HashMap<>();
		responseObj.put("value", "GET Response of secured resource");
		Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseObj);
		return builder.build();
	}

	@GET
	@Path("/secureWithAdminRole")
	@Secured({ Role.Admin })
	@Produces(MediaType.APPLICATION_JSON)
	public Response getC() {
		Map<String, String> responseObj = new HashMap<>();
		responseObj.put("value", "GET Response of secured resource with admin role");
		Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseObj);
		return builder.build();
	}

}
