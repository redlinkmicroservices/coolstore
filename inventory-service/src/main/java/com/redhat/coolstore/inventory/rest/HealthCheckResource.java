package com.redhat.coolstore.inventory.rest;

import javax.ws.rs.Path;

import org.wildfly.swarm.health.HealthStatus;

@Path("/")
public class HealthCheckResource {

    // TODO: Add annotations to make this method provide health status for this service at the path /status
    public HealthStatus check() {
	// TODO: Return the health status as "UP"
	return null;
    }

}
