package com.redhat.coolstore.gateway.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.redhat.coolstore.gateway.model.ShoppingCart;
import com.redhat.coolstore.gateway.proxy.CartResource;


@Path("cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class CartGateway {
	
	@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject
	@ConfigProperty(name="CART_SERVICE_URL")
	private String cartURL;
	
	private CartResource buildClient() {
		System.out.println("Building new client " + cartURL);
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(cartURL);
		ResteasyWebTarget restEasyTarget = (ResteasyWebTarget)target;
		return restEasyTarget.proxy(CartResource.class);
		
	}
	
	@GET
	@Path("/{cartId}")
	public ShoppingCart getCart(@PathParam("cartId") String cartId) {
		CartResource proxy = buildClient();
		return proxy.getCart(cartId);
	}
	
	@POST
	@Path("/{cartId}/{itemId}/{quantity}")
	public ShoppingCart addToCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId, @PathParam("quantity") int quantity) {
		CartResource proxy = buildClient();
		return proxy.addToCart(cartId, itemId, quantity);
	}

	@DELETE
	@Path("/{cartId}/{itemId}/{quantity}")
	public ShoppingCart removeFromCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId, @PathParam("quantity") int quantity) {
		CartResource proxy = buildClient();
		return proxy.removeFromCart(cartId, itemId, quantity);
	}
	@POST
	@Path("/checkout/{cartId}")
	public ShoppingCart checkout(@PathParam("cartId")  String cartId) {
		CartResource proxy = buildClient();
		return proxy.checkout(cartId);
	}
}
