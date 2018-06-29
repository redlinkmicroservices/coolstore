package com.redhat.coolstore.gateway.api.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.redhat.coolstore.gateway.api.CartGateway;
import com.redhat.coolstore.gateway.proxy.CartResource;
@Category(UnitTests.class)
public class CartGatewayAddToCartCircuitBreakerTest {

	@Mock
	private CartResource proxy;

	@Before
	public  void before() throws Exception {
		MockitoAnnotations.initMocks(this);
		ConfigurationManager.getConfigInstance().clear();
		Hystrix.reset();
		warmUpAddToCartCircuitBreaker();
		openCircuitBreakerAfterOneFailingRequest();

	}
	
	@After
	public void after() {
		MockitoAnnotations.initMocks(this);
		ConfigurationManager.getConfigInstance().clear();
		Hystrix.reset();
	}
	
	@Test
	public void testAddToCartCircuitBreaker() throws Exception {
		doThrow(RuntimeException.class).when(proxy).addToCart("1","2", 10);
		HystrixCircuitBreaker circuitBreaker = getCircuitBreaker();

		// demonstrates circuit is actually closed
		assertThat(circuitBreaker.isOpen(), equalTo(false));
		assertThat(circuitBreaker.allowRequest(), equalTo(true));

		CartGateway.AddToCartCommand command = new CartGateway.AddToCartCommand(proxy, "1","2",10);
		try {
			command.execute();
			fail();
		} catch (HystrixRuntimeException e) {
            waitUntilCircuitBreakerOpens();
			assertThat(circuitBreaker.isOpen(), is(true));
		}
	}

	private void waitUntilCircuitBreakerOpens() throws InterruptedException {
		Thread.sleep(1000);
	}


	private void warmUpAddToCartCircuitBreaker() {
		when(proxy.addToCart("1","2",10)).thenReturn(null);
		CartGateway.AddToCartCommand command = new CartGateway.AddToCartCommand(proxy, "1","2",10);
		command.execute();
	}

	public static HystrixCircuitBreaker getCircuitBreaker() {
		return HystrixCircuitBreaker.Factory.getInstance(getCommandKey());
	}

	private static HystrixCommandKey getCommandKey() {
		return HystrixCommandKey.Factory.asKey(CartGateway.AddToCartCommand.ADD_TO_CART_COMMAND_KEY);
	}

	private void openCircuitBreakerAfterOneFailingRequest() {
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.command." + CartGateway.AddToCartCommand.ADD_TO_CART_COMMAND_KEY+ ".circuitBreaker.requestVolumeThreshold", 1);
	}

}
