package com.redhat.coolstore.catalog.model;

import java.io.Serializable;

import io.vertx.core.json.JsonObject;
public class Product implements Serializable {

    private static final long serialVersionUID = -6994655395272795259L;
    
    private String itemId;
    private String name;
    private String desc;
    private double price;
    
    public Product() {
        
    }
    public Product(JsonObject object) {
    	itemId=object.getString("itemId");
    	name=object.getString("name");
    	desc=object.getString("desc");
    	price=object.getDouble("price");
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public JsonObject toJson() {
    	JsonObject jsonObject = new JsonObject();
    	jsonObject.put("itemId", itemId);
    	jsonObject.put("name", name);
    	jsonObject.put("desc", desc);
    	jsonObject.put("price", price);
        return jsonObject;
    }
}
