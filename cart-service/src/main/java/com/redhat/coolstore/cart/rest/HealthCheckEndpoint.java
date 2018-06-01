package com.redhat.coolstore.cart.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/")
@Component
public class HealthCheckEndpoint {

	//TODO inject a HealthEndpoint

	@GET
	@Path("/health")
	@Produces(MediaType.APPLICATION_JSON)
	//TODO change the return type
	public String getHealth() {
		//TODO invoke the HealthEndpoint and return the result Health object
		return null;
	}
}
