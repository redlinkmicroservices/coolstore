package com.redhat.coolstore.inventory.service;

import javax.persistence.EntityManager;
import com.redhat.coolstore.inventory.model.Inventory;

// Add appropriate scope for this class
public class InventoryService {

    // Inject the "primary" persistence context
    private EntityManager em;

    public Inventory getInventory(String itemId) {
        // Use the entity manager to look up a single inventory item
	// based on item id, and then return it.
        return null;
    }

}
