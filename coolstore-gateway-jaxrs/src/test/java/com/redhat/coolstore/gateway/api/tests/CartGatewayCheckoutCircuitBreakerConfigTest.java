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

public class CartGatewayCheckoutCircuitBreakerConfigTest {

		@Mock
		private CartResource proxy;

		@Before
		public void before() throws Exception {
			MockitoAnnotations.initMocks(this);
			warmUpCheckoutCircuitBreaker();

		}


	    @Test
	    public void testCheckoutFromCartTimeout() {
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.CheckoutCommand.CHECKOUT_CART_COMMAND_KEY).circuitBreakerRequestVolumeThreshold().get() == 2);
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.CheckoutCommand.CHECKOUT_CART_COMMAND_KEY).circuitBreakerSleepWindowInMilliseconds().get() == 5000);
	    }

	    


	    private void warmUpCheckoutCircuitBreaker() {
			when(proxy.checkout("1")).thenReturn(null);
			CartGateway.CheckoutCommand command = new CartGateway.CheckoutCommand(proxy, "1");
			command.execute();
			
		}

	    
	    public static HystrixCommandProperties getCircuitBreakerCommandProperties(String commandKey) {
	        return HystrixCommandMetrics.getInstance(getCommandKey(commandKey)).getProperties();
	    }

	    
	    private static HystrixCommandKey getCommandKey(String commandKey) {
	        return HystrixCommandKey.Factory.asKey(commandKey);
	    }
}
