package com.redhat.coolstore.cart.service;

import com.redhat.coolstore.cart.model.Product;

//TODO mark this class as a Spring managed bean
public class CatalogServiceImpl implements CatalogService {

	//TODO mark this attribute as a configuration value
	private String catalogServiceUrl;

	@Override
	public Product getProduct(String itemId) {
		//TODO use Spring Web RestTemplate to invoke the catalog service /product/{id} end point
        return null;
	}
	
}
