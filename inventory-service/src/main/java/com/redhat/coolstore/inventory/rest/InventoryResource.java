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

// Add annotations to class
//   * Map this resource class to the path /inventory
//   * This resource class should be request scoped
public class InventoryResource {

  // Inject the service class for this microservice

  // Add annotations to this method
  //   * should respond to HTTP GET requests
  //   * should respond to requests for the path /inventory/<itemid>
  //   * Should return a single Inventory object in JSON format
  //   * Accept a single string argument called "itemId"
  public Inventory getInventory(String itemId) {
  
    // Invoke the getInventory(String itemid) method on the service class
    // which returns the Inventory object which matches the itemId

    // Check if the returned Inventory object is null,
    // if so, throw NotFoundException, otherwise
    // return the inventory object
    return null;
  }
}
