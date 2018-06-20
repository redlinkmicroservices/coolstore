package com.redhat.coolstore.gateway.proxy;

import java.util.List;

import com.redhat.coolstore.gateway.model.Product;


// TODO: Add annotations for media types
public interface ProductResource {
	
	// TODO: Add annotations
	public List<Product> getProducts();
	
	// TODO: Add annotations for method and the "itemId" param
	public Product getProduct(String itemId);

	// TODO: Add annotations
	public void addProduct(Product product);

}
