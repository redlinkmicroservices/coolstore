package com.redhat.coolstore.gateway.api.tests;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.redhat.coolstore.gateway.api.CartGateway;
import com.redhat.coolstore.gateway.proxy.CartResource;

public class CartGatewayAddToCartCircuitBreakerConfigTest {

		@Mock
		private CartResource proxy;

		@Before
		public void before() throws Exception {
			MockitoAnnotations.initMocks(this);
			Hystrix.reset();
			ConfigurationManager.getConfigInstance().clear();
			warmUpAddToCartCircuitBreaker();
		}
		@After
		public void after() throws Exception{
			Hystrix.reset();
			ConfigurationManager.getConfigInstance().clear();
		}
		
	    @Test
	    public void testAddToCartTimeout() {
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.AddToCartCommand.ADD_TO_CART_COMMAND_KEY).circuitBreakerRequestVolumeThreshold().get() == 2);
	        assertTrue(getCircuitBreakerCommandProperties(CartGateway.AddToCartCommand.ADD_TO_CART_COMMAND_KEY).circuitBreakerSleepWindowInMilliseconds().get() == 5000);
	    }


	    private void warmUpAddToCartCircuitBreaker() {
			when(proxy.addToCart("1","2",10)).thenReturn(null);
			CartGateway.AddToCartCommand command = new CartGateway.AddToCartCommand(proxy, "1","2",10);
			command.execute();
	    }


	    public static HystrixCommandProperties getCircuitBreakerCommandProperties(String commandKey) {
	        return HystrixCommandMetrics.getInstance(getCommandKey(commandKey)).getProperties();
	    }

	    private static HystrixCommandKey getCommandKey(String commandKey) {
	        return HystrixCommandKey.Factory.asKey(commandKey);
	    }
}
