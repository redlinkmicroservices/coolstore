package com.redhat.coolstore.inventory.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wildfly.swarm.health.Health;
import org.wildfly.swarm.health.HealthStatus;

@Path("/")
public class HealthCheckResource {

    // Add annotations to make this method
    // provide health status for this service at the path /status
    public HealthStatus check() {
	// Return the health status as "UP"
	return null;
    }

}
