package com.redhat.coolstore.cart.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.redhat.coolstore.cart.service.CatalogService;
import com.redhat.coolstore.cart.service.ShoppingCartService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartHystrixTest {

	@Autowired
	private CartEndpoint endpoint;
	@MockBean
	private CatalogService proxy;
	@MockBean
	private ShoppingCartService shoppingCartService;

	@Before
	public void setup() {
		resetHystrix();
	}

	@Test
	public void shouldRemoveCartCircuit() throws InterruptedException {
		warmUpRemoveCartCircuitBreaker();
		openCircuitBreakerAfterOneFailingRequest(CartEndpoint.REMOVE_CART_ENDPOINT_KEY);

		willThrow(new RuntimeException()).given(shoppingCartService).removeFromCart("1","1",200);
		HystrixCircuitBreaker circuitBreaker = getCircuitBreaker(CartEndpoint.REMOVE_CART_ENDPOINT_KEY);

		// demonstrates circuit is actually closed
		assertFalse(circuitBreaker.isOpen());
		assertTrue(circuitBreaker.allowRequest());

		try {
			endpoint.removeFromCart("1","1",200);
			fail("unexpected");
		} catch (RuntimeException exception) {
			waitUntilCircuitBreakerOpens();
			assertTrue(circuitBreaker.isOpen());
			assertFalse(circuitBreaker.allowRequest());
		}
	}
	
	@Test
	public void shouldGetCartCircuit() throws InterruptedException {
		warmUpGetCartCircuitBreaker();
		openCircuitBreakerAfterOneFailingRequest(CartEndpoint.GET_CART_ENDPOINT_KEY);

		willThrow(new RuntimeException()).given(shoppingCartService).getShoppingCart("1");
		HystrixCircuitBreaker circuitBreaker = getCircuitBreaker(CartEndpoint.GET_CART_ENDPOINT_KEY);

		// demonstrates circuit is actually closed
		assertFalse(circuitBreaker.isOpen());
		assertTrue(circuitBreaker.allowRequest());

		try {
			endpoint.getCart("1");
			fail("unexpected");
		} catch (RuntimeException exception) {
			waitUntilCircuitBreakerOpens();
			assertTrue(circuitBreaker.isOpen());
			assertFalse(circuitBreaker.allowRequest());
		}
	}

	@Test
	public void shouldAddCartCircuit() throws InterruptedException {
		warmUpAddCartCircuitBreaker();
		openCircuitBreakerAfterOneFailingRequest(CartEndpoint.ADD_CART_ENDPOINT_KEY);

		willThrow(new RuntimeException()).given(shoppingCartService).addToCart("1", "1",100);
		HystrixCircuitBreaker circuitBreaker = getCircuitBreaker(CartEndpoint.ADD_CART_ENDPOINT_KEY);

		// demonstrates circuit is actually closed
		assertFalse(circuitBreaker.isOpen());
		assertTrue(circuitBreaker.allowRequest());

		try {
			endpoint.addToCart("1", "1", 100);
			fail("unexpected");
		} catch (RuntimeException exception) {
			waitUntilCircuitBreakerOpens();
			assertTrue(circuitBreaker.isOpen());
			assertFalse(circuitBreaker.allowRequest());
		}
	}

	
	private void waitUntilCircuitBreakerOpens() throws InterruptedException {
		Thread.sleep(1000);
	}

	private void resetHystrix() {
		Hystrix.reset();
	}

	private void warmUpGetCartCircuitBreaker() {
		when(shoppingCartService.getShoppingCart("1")).thenReturn(null);
		endpoint.getCart("1");
	}
	

	private void warmUpAddCartCircuitBreaker() {
		when(shoppingCartService.addToCart("1", "1", 10)).thenReturn(null);
		endpoint.addToCart("1","1",10);
	}
	

	private void warmUpRemoveCartCircuitBreaker() {
		when(shoppingCartService.removeFromCart("1", "1", 10)).thenReturn(null);
		endpoint.removeFromCart("1", "1", 10);
	}

	public static HystrixCircuitBreaker getCircuitBreaker(String key) {
		return HystrixCircuitBreaker.Factory.getInstance(getCommandKey(key));
	}

	private static HystrixCommandKey getCommandKey(String command) {
		return HystrixCommandKey.Factory.asKey(command);
	}

	private void openCircuitBreakerAfterOneFailingRequest(String key) {
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.command." + key + ".circuitBreaker.requestVolumeThreshold", 1);
	}
}
