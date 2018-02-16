package com.redhat.coolstore.gateway.api;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.redhat.coolstore.gateway.model.Product;
import com.redhat.coolstore.gateway.proxy.ProductResource;


@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProductGateway {
	
	@ConfigProperty(name="CATALOG_SERVICE_URL")
	private String catalogServiceUrl;
	
	
	private ProductResource buildClient() {
		System.out.println("Building new client");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(catalogServiceUrl);
		ResteasyWebTarget restEasyTarget = (ResteasyWebTarget)target;
		return restEasyTarget.proxy(ProductResource.class);
		
	}
	

	@GET
	@Path("/products")
	public List<Product> getProducts() {
		ProductResource proxy = buildClient();
		return proxy.getProducts();
	}
	
	@GET
	@Path("/product/{itemId}")
	public Product getProduct(@PathParam("itemId") String itemId) {
		ProductResource proxy = buildClient();
		return proxy.getProduct(itemId);
	}

	@POST
	@Path("/product")
	public void addProduct(Product product) {
		ProductResource proxy = buildClient();
		proxy.addProduct(product);
	}

}
