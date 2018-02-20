package com.redhat.coolstore.inventory.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.redhat.coolstore.inventory.model.Inventory;
import com.redhat.coolstore.inventory.service.InventoryService;

@Path("/inventory")
@RequestScoped
public class InventoryResource {

	@Inject
	private InventoryService inventoryService;

	@Inject
	@ConfigurationValue("hystrix.inventory.circuitBreaker.requestVolumeThreshold")
	private int hystrixCircuitBreakerRequestVolumeThreshold;

	@Inject
	@ConfigurationValue("hystrix.inventory.groupKey")
	private String hystrixGroupKey;

	@GET
	@Path("/{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory getInventory(@PathParam("itemId") String itemId) {
		try {
			Inventory inventory = new GetInventoryCommand(itemId).execute();

			if (inventory == null) {
				throw new NotFoundException();
			} else {
				return inventory;
			}
		} catch (HystrixRuntimeException e) {
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
		}
	}

	class GetInventoryCommand extends HystrixCommand<Inventory> {

		private String itemId;

		public GetInventoryCommand(String itemId) {
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey))
					.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
							.withCircuitBreakerRequestVolumeThreshold(hystrixCircuitBreakerRequestVolumeThreshold)));
			this.itemId = itemId;
		}

		@Override
		protected Inventory run() throws Exception {
			return inventoryService.getInventory(itemId);
		}
	}
}
