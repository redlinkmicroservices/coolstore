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

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.redhat.coolstore.gateway.model.ShoppingCart;
import com.redhat.coolstore.gateway.proxy.CartResource;

@Path("cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class CartGateway {

	@Inject
	@ConfigProperty(name = "CART_SERVICE_URL")
	private String cartURL;


	private CartResource buildClient() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(cartURL);
		ResteasyWebTarget restEasyTarget = (ResteasyWebTarget) target;
		return restEasyTarget.proxy(CartResource.class);
	}

	@GET
	@Path("/{cartId}")
	public ShoppingCart getCart(@PathParam("cartId") String cartId) {
		CartResource proxy = buildClient();
		return new GetCartCommand(proxy,cartId).execute();
	}

	public static class GetCartCommand extends HystrixCommand<ShoppingCart>{
		public final static String GET_CART_COMMAND_KEY="GetCartCommandKey";
		private CartResource proxy;
		private String cartId;

		public GetCartCommand(CartResource proxy, String cartId) {
			super(Setter
					.withGroupKey(HystrixCommandGroupKey.Factory.asKey("group"))
					.andCommandKey(HystrixCommandKey.Factory.asKey(GET_CART_COMMAND_KEY))
					.andCommandPropertiesDefaults(
							HystrixCommandProperties
								.Setter()
									.withCircuitBreakerRequestVolumeThreshold(2)
									.withCircuitBreakerSleepWindowInMilliseconds(5000)));
			this.cartId=cartId;
			this.proxy=proxy;
		}

		@Override
		protected ShoppingCart run() throws Exception {
			return proxy.getCart(cartId);
		}
		
	}
	
	@POST
	@Path("/{cartId}/{itemId}/{quantity}")
	public ShoppingCart addToCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId,
			@PathParam("quantity") int quantity) {
		CartResource proxy = buildClient();
		
		//TODO use the HystrixCommand created for the addToCart method and invoke the execute method 
		return proxy.addToCart(cartId, itemId, quantity);
	}

	//TODO Create a static class that extends HystrixCommand for the addToCart method
	
	@DELETE
	@Path("/{cartId}/{itemId}/{quantity}")
	public ShoppingCart removeFromCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId,
			@PathParam("quantity") int quantity) {
		CartResource proxy = buildClient();
		return proxy.removeFromCart(cartId, itemId, quantity);
	}


	
	@POST
	@Path("/checkout/{cartId}")
	public ShoppingCart checkout(@PathParam("cartId") String cartId) {
		CartResource proxy = buildClient();
		return proxy.checkout(cartId);
	}
	


}
