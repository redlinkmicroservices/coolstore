package com.redhat.coolstore.cart.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.redhat.coolstore.cart.model.Product;

@Component
public class CatalogServiceImpl implements CatalogService {

	@Value("${catalog.service.url}")
	private String catalogServiceUrl;

	@Override
	public Product getProduct(String itemId) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Product> entity = restTemplate.getForEntity(catalogServiceUrl + "/product/" + itemId, Product.class);
			return entity.getBody();
		}
		catch (HttpClientErrorException ex) {
			if (ex.getRawStatusCode() == 404)
				return null;
			else
				throw (ex);
		}
	}
	
}
