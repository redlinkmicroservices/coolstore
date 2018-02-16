package com.redhat.coolstore.gateway.proxy;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.redhat.coolstore.gateway.model.ShoppingCart;

@Path("/cart")
@Produces("application/json")
public interface CartResource {
	
	@GET
	@Path("/{cartId}")
	public ShoppingCart getCart(@PathParam("cartId") String cartId);
	
	@POST
	@Path("/{cartId}/{itemId}/{quantity}")
	public ShoppingCart addToCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId, @PathParam("quantity") int quantity);

	@DELETE
	@Path("/{cartId}/{itemId}/{quantity}")
	public ShoppingCart removeFromCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId, @PathParam("quantity") int quantity);
	@POST
	@Path("/checkout/{cartId}")
	public ShoppingCart checkout(@PathParam("cartId")  String cartId);
}
