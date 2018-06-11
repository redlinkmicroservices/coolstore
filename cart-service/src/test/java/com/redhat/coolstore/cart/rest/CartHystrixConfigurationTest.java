package com.redhat.coolstore.cart.rest;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import com.redhat.coolstore.cart.service.CatalogService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CartHystrixConfigurationTest {

	@MockBean
	private CatalogService proxy;
	
	@Autowired
	private CartEndpoint endpoint;
	
	
	@BeforeClass	
	public static void setupClass() {
    	System.setProperty("catalog.service.url", "http://localhost:7071");
	}
	
    @Before
    public void setup() {
        warmUpGetCartCircuitBreaker();
        warmUpAddCartCircuitBreaker();
        warmUpRemoveCartCircuitBreaker();
        warmUpCheckoutCartCircuitBreaker();
    }

    private void warmUpGetCartCircuitBreaker() {
        endpoint.getCart("1");
    }

    private void warmUpAddCartCircuitBreaker() {
        endpoint.addToCart("1", "1", 10);
    }
    private void warmUpRemoveCartCircuitBreaker() {
        endpoint.removeFromCart("1", "1", 100);
    }
    private void warmUpCheckoutCartCircuitBreaker() {
        endpoint.checkout("1");
    }

    
    public static HystrixCommandProperties getCircuitBreakerCommandProperties(String key) {
        return HystrixCommandMetrics.getInstance(getCommandKey(key)).getProperties();
    }

    private static HystrixCommandKey getCommandKey(String key) {
        return HystrixCommandKey.Factory.asKey(key);
    }
    
	@Test
	public void testGetCartCircuitBreaker() {
		
        HystrixCommandProperties circuitBreakerCommandProperties = getCircuitBreakerCommandProperties(CartEndpoint.GET_CART_ENDPOINT_KEY);
		HystrixProperty<Integer> executionTimeoutInMilliseconds = circuitBreakerCommandProperties.executionTimeoutInMilliseconds();
		assertTrue(executionTimeoutInMilliseconds.get() == 1000);
	}

	@Test
	public void testAddCartCircuitBreaker() {
		
        HystrixProperty<Integer> executionTimeoutInMilliseconds = getCircuitBreakerCommandProperties(CartEndpoint.ADD_CART_ENDPOINT_KEY).executionTimeoutInMilliseconds();
		assertTrue(executionTimeoutInMilliseconds.get() == 1000);
	}

	@Test
	public void testRemoveCartCircuitBreaker() {
		
        HystrixProperty<Integer> executionTimeoutInMilliseconds = getCircuitBreakerCommandProperties(CartEndpoint.REMOVE_CART_ENDPOINT_KEY).executionTimeoutInMilliseconds();
		assertTrue(executionTimeoutInMilliseconds.get() == 1000);
	}

	@Test
	public void testCheckoutCartCircuitBreaker() {
		
        HystrixProperty<Integer> executionTimeoutInMilliseconds = getCircuitBreakerCommandProperties(CartEndpoint.CHECKOUT_CART_ENDPOINT_KEY).executionTimeoutInMilliseconds();
		assertTrue(executionTimeoutInMilliseconds.get() == 1000);
	}

	
}
