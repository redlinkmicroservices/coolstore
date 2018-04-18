package com.redhat.coolstore.inventory.rest;

import com.redhat.coolstore.inventory.model.Inventory;

// TODO: Add annotation to map this resource class to the path /inventory
// TODO: Add annotation to make this class request scoped
public class InventoryResource {

  // TODO: Inject the service class for this microservice

  // TODO: Add annotation to respond to HTTP GET requests
  // TODO: Add annotation to respond to requests for the path /inventory/{itemId}
  // TODO: Add annotation to return a single Inventory object in JSON format
  // TODO: Add annotation to accept a single string argument called "itemId"
  public Inventory getInventory(String itemId) {
  
    // TODO: Invoke the getInventory(String itemid) method on the service class

    // TODO: Check if the returned Inventory object is null,
    // TODO: if so, throw NotFoundException, otherwise
    // TODO: return the inventory object
    return null;
  }
}
