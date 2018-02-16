package com.redhat.coolstore.gateway.proxy;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.redhat.coolstore.gateway.model.Product;


@Produces("application/json")
public interface ProductResource {
	
	@GET
	@Path("/products")
	public List<Product> getProducts();
	
	@GET
	@Path("/product/{itemId}")
	public Product getProduct(@PathParam("itemId") String itemId);

	@POST
	@Path("/product")
	public void addProduct(Product product);

}
