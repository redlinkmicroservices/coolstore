package com.redhat.coolstore.inventory;

import javax.enterprise.inject.Specializes;

import com.redhat.coolstore.inventory.model.Inventory;
import com.redhat.coolstore.inventory.service.InventoryService;

@Specializes
public class ErrorInventoryService extends InventoryService {
	
    @Override
    public Inventory getInventory(String itemId) {
        if ("error".equalsIgnoreCase(itemId)) {
            throw new RuntimeException();
        }
        if ("timeout".equalsIgnoreCase(itemId)) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
        }
        return super.getInventory(itemId);
    }

}
