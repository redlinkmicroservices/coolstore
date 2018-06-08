package com.redhat.coolstore.gateway.api.tests;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.redhat.coolstore.gateway.api.CartGateway;
import com.redhat.coolstore.gateway.proxy.CartResource;

public class CartGatewayCircuitBreakerConfigTest {

		@Mock
		private CartResource proxy;

		@Before
		public void before() throws Exception {
			MockitoAnnotations.initMocks(this);
			warmUpGetCartCircuitBreaker();
			warmUpAddToCartCircuitBreaker();
			warmUpRemoveCartCircuitBreaker();

			warmUpCheckoutCircuitBreaker();

		}


		@Test
	    public void testGetCartTimeout() {
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.GetCartCommand.GET_CART_COMMAND_KEY).circuitBreakerRequestVolumeThreshold().get() == 2);
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.GetCartCommand.GET_CART_COMMAND_KEY).circuitBreakerSleepWindowInMilliseconds().get() == 5000);
	    }

	    @Test
	    public void testAddToCartTimeout() {
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.AddToCartCommand.ADD_TO_CART_COMMAND_KEY).circuitBreakerRequestVolumeThreshold().get() == 2);
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.AddToCartCommand.ADD_TO_CART_COMMAND_KEY).circuitBreakerSleepWindowInMilliseconds().get() == 5000);
	    }

	    @Test
	    public void testRemoveFromCartTimeout() {
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.RemoveFromCartCommand.REMOVE_CART_COMMAND_KEY).circuitBreakerRequestVolumeThreshold().get() == 2);
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.RemoveFromCartCommand.REMOVE_CART_COMMAND_KEY).circuitBreakerSleepWindowInMilliseconds().get() == 5000);
	    }
	    @Test
	    public void testCheckoutFromCartTimeout() {
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.CheckoutCommand.CHECKOUT_CART_COMMAND_KEY).circuitBreakerRequestVolumeThreshold().get() == 2);
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.CheckoutCommand.CHECKOUT_CART_COMMAND_KEY).circuitBreakerSleepWindowInMilliseconds().get() == 5000);
	    }

	    

		private void warmUpGetCartCircuitBreaker() {
			when(proxy.getCart("1")).thenReturn(null);
			CartGateway.GetCartCommand command = new CartGateway.GetCartCommand(proxy, "1");
			command.execute();
	    }

	    private void warmUpAddToCartCircuitBreaker() {
			when(proxy.addToCart("1","2",10)).thenReturn(null);
			CartGateway.AddToCartCommand command = new CartGateway.AddToCartCommand(proxy, "1","2",10);
			command.execute();
	    }

	    private void warmUpCheckoutCircuitBreaker() {
			when(proxy.checkout("1")).thenReturn(null);
			CartGateway.CheckoutCommand command = new CartGateway.CheckoutCommand(proxy, "1");
			command.execute();
			
		}
		private void warmUpRemoveCartCircuitBreaker() {
			when(proxy.removeFromCart("1","2",10)).thenReturn(null);
			CartGateway.RemoveFromCartCommand command = new CartGateway.RemoveFromCartCommand(proxy, "1","2",10);
			command.execute();
		}

	    
	    public static HystrixCommandProperties getCircuitBreakerCommandProperties(String commandKey) {
	        return HystrixCommandMetrics.getInstance(getCommandKey(commandKey)).getProperties();
	    }

	    
	    private static HystrixCommandKey getCommandKey(String commandKey) {
	        return HystrixCommandKey.Factory.asKey(commandKey);
	    }
}
