package com.redhat.coolstore.cart.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.service.ShoppingCartService;

@Path("/cart")
@Component
public class CartEndpoint {
	
	private static final Logger LOG = LoggerFactory.getLogger(CartEndpoint.class);
	
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	@GET
	@Path("/{cartId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ShoppingCart getCart(@PathParam("cartId") String cartId) {
		System.out.println("*** getCart: '" + cartId + "'");
		ShoppingCart cart = shoppingCartService.getShoppingCart(cartId);
		System.out.println("*** cart.getId: '" + cart.getId() + "'");
		return cart;
	}
	
	@POST
	@Path("/{cartId}/{itemId}/{quantity}")
	@Produces(MediaType.APPLICATION_JSON)
	public ShoppingCart addToCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId, @PathParam("quantity") int quantity) {
		return shoppingCartService.addToCart(cartId, itemId, quantity);
	}

	@DELETE
	@Path("/{cartId}/{itemId}/{quantity}")
	@Produces(MediaType.APPLICATION_JSON)
	public ShoppingCart removeFromCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId, @PathParam("quantity") int quantity) {
		return shoppingCartService.removeFromCart(cartId, itemId, quantity);
	}
	
	@POST
	@Path("/checkout/{cartId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ShoppingCart checkout(@PathParam("cartId")  String cartId) {
		LOG.info("Checkout: " + cartId);
		//XXX noop
		return shoppingCartService.getShoppingCart(cartId);
	}
	
}
