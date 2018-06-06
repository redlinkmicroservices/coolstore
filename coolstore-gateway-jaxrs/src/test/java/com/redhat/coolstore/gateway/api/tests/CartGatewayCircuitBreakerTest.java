package com.redhat.coolstore.gateway.api.tests;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.redhat.coolstore.gateway.api.RestApplication;
import com.redhat.coolstore.gateway.model.ShoppingCartItem;
import com.redhat.coolstore.gateway.proxy.CartResource;

@Category(UnitTests.class)
@RunWith(Arquillian.class)
public class CartGatewayCircuitBreakerTest {

	private static String port = System.getProperty("arquillian.swarm.http.port", "18080");
	@Rule
	public WireMockRule cartMockRule = new WireMockRule(options().port(7071));
	
	
	private Client client;
	
	
	@CreateSwarm
	public static Swarm newContainer() throws Exception {
		Properties properties = new Properties();
		properties.put("swarm.http.port", port);
		return new Swarm(properties);
	}

	@Deployment
	public static Archive<?> createDeployment() {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "coolstore-gateway.war")
				.addPackages(true, RestApplication.class.getPackage())
				.addPackages(true, ShoppingCartItem.class.getPackage())
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsManifestResource("config-test.properties", "microprofile-config.properties")
				.addPackages(true, CartResource.class.getPackage()).addClass(UnitTests.class);

		return archive;
	}

	@Before
	public void before() throws Exception {
		client = ClientBuilder.newClient();
	}

	@After
	public void after() throws Exception {
		client.close();
	}
	
	@Test
	@RunAsClient
	public void shouldHaveCustomTimeout() {

		cartMockRule.stubFor(get(urlMatching("/cart/[a-zA-Z]+"))
		        .willReturn(WireMock.serverError()));

		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/mycart");
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		System.out.println(response);
		String result = response.readEntity(String.class);
		System.out.println(result);

		assertThat(response.getStatus(), is(500));
		HystrixCommandKey groupKey = HystrixCommandKey.Factory.asKey("group");
		HystrixCircuitBreaker instanceCart = HystrixCircuitBreaker.Factory.getInstance(groupKey);
		System.out.println(instanceCart);
	}


}
