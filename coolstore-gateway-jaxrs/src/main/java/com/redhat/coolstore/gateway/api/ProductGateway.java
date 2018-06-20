package com.redhat.coolstore.gateway.api;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.redhat.coolstore.gateway.model.Product;
import com.redhat.coolstore.gateway.proxy.ProductResource;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProductGateway {

	@Inject
	@ConfigProperty(name = "CATALOG_SERVICE_URL")
	private String catalogServiceUrl;


	private ProductResource buildClient() {
		// TODO: Create and return the ProductResource proxy
		return null;
	}

	// TODO: Add annotations
	public List<Product> getProducts() {
		ProductResource proxy = buildClient();
		return proxy.getProducts();
	}

	// TODO: Add annotations to method and "itemId" param
	public Product getProduct(String itemId) {
		ProductResource proxy = buildClient();
		return proxy.getProduct(itemId);
	}

	// TODO: Add annotations
	public void addProduct(final Product product) {
		ProductResource proxy = buildClient();
		proxy.addProduct(product);
	}

	


}
