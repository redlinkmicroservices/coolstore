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

import com.redhat.coolstore.inventory.model.Inventory;
import com.redhat.coolstore.inventory.service.InventoryService;

@Path("/inventory")
@RequestScoped
public class InventoryResource {

	@Inject
	private InventoryService inventoryService;

	@GET
	@Path("/{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory getInventory(@PathParam("itemId") String itemId) {
			Inventory inventory = inventoryService.getInventory(itemId);

			if (inventory == null) {
				throw new NotFoundException();
			} else {
				return inventory;
	   }
  }
}
