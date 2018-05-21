package com.redhat.coolstore.inventory.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wildfly.swarm.health.HealthStatus;

@Path("/")
public class HealthCheckResource {

	@GET
	@Path("/status")
	// TODO: Add annotations to make this method provide health status for this service
    public HealthStatus check() {
	    // TODO: Return the health status as "UP"
	    return null;
    }

}
