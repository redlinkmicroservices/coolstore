package com.redhat.coolstore.inventory.service;

import javax.persistence.EntityManager;
import com.redhat.coolstore.inventory.model.Inventory;

// TODO: Add appropriate scope for this class
public class InventoryService {

    // TODO: Inject the "primary" persistence context
    private EntityManager em;

    public Inventory getInventory(String itemId) {
        // TODO: Use the entity manager to look up a single inventory item based on item id, and then return it.
        return null;
    }

}
