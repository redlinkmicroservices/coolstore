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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.redhat.coolstore.gateway.model.ShoppingCart;
import com.redhat.coolstore.gateway.proxy.CartResource;

@SuppressWarnings("cdi-ambiguous-dependency")
@Path("cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class CartGateway {

	@Inject
	@ConfigProperty(name = "CART_SERVICE_URL", defaultValue = "http://cart-service-coolstore-cart.192.168.99.100.nip.io:80")
	private String cartURL;

	@Inject
	@ConfigurationValue("hystrix.cart.executionTimeout")
	private int hystrixExecutionTimeout;

	@Inject
	@ConfigurationValue("hystrix.cart.groupKey")
	private String hystrixGroupKey;

	@Inject
	@ConfigurationValue("hystrix.cart.circuitBreakerEnabled")
	private boolean hystrixCircuitBreakerEnabled;

	private CartResource buildClient() {
		System.out.println("group key*******" + hystrixGroupKey);
		Client client = ClientBuilder.newClient();
		try {
			CartResource cartResource = new GetWebTarget(client).execute();
			if (cartResource == null) {
				throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
			} else {
				return cartResource;
			}

		}  catch (HystrixRuntimeException e) {
			throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
		}

	}

	@GET
	@Path("/{cartId}")
	public ShoppingCart getCart(@PathParam("cartId") String cartId) {
		CartResource proxy = buildClient();
		return proxy.getCart(cartId);
	}

	@POST
	@Path("/{cartId}/{itemId}/{quantity}")
	public ShoppingCart addToCart(@PathParam("cartId") String cartId, @PathParam("itemId") String itemId,
			@PathParam("quantity") int quantity) {
		CartResource proxy = buildClient();
		return proxy.addToCart(cartId, itemId, quantity);
	}

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

	public class GetWebTarget extends HystrixCommand<CartResource> {
		private Client client;

		public GetWebTarget(Client client) {
			
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey))
					.andCommandPropertiesDefaults(
							HystrixCommandProperties.Setter().withCircuitBreakerEnabled(hystrixCircuitBreakerEnabled)
									.withExecutionTimeoutInMilliseconds(hystrixExecutionTimeout)));
			this.client = client;

		}

		@Override
		protected CartResource run() throws Exception {
			WebTarget target = client.target(cartURL);
			ResteasyWebTarget restEasyTarget = (ResteasyWebTarget) target;
			return restEasyTarget.proxy(CartResource.class);
		}
	}

}
