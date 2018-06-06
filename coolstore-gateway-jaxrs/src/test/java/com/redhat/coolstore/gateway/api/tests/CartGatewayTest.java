package com.redhat.coolstore.gateway.api.tests;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.redhat.coolstore.gateway.api.RestApplication;
import com.redhat.coolstore.gateway.model.ShoppingCartItem;
import com.redhat.coolstore.gateway.proxy.CartResource;

@Category(UnitTests.class)
@RunWith(Arquillian.class)
public class CartGatewayTest {

	@Rule
	public WireMockRule cartMockRule = new WireMockRule(options().port(7071));

	private static String port = System.getProperty("arquillian.swarm.http.port", "18080");

	private Client client;

	@CreateSwarm
	public static Swarm newContainer() throws Exception {
		Properties properties = new Properties();
		properties.put("swarm.http.port", port);
		return new Swarm(properties).withProfile("local");
	}

	@Deployment
	public static Archive<?> createDeployment() {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "coolstore-gateway.war")
				.addPackages(true, RestApplication.class.getPackage())
				.addPackages(true, ShoppingCartItem.class.getPackage())
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsManifestResource("config-test.properties","microprofile-config.properties")
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
	public void testGetCart() throws Exception {

		// Mock the Cart service for getting single ShoppingCart instance
		cartMockRule.stubFor(get(urlMatching("/cart/[a-zA-Z]+"))
		        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
		        .withBodyFile("mycart-empty.json")));

		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/mycart");
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("id", null), equalTo("mycart"));

	}

	@Test
	@RunAsClient
	public void testAddCart() throws Exception {

		// Mock the Cart service to add items to a cart
		cartMockRule.stubFor(post(urlMatching("/cart/[a-zA-Z]+/[0-9]+/[0-9]+"))
		        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
		        .withBodyFile("mycart-with-items.json")));

		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/mycart")
				.path("/165614").path("/2");
		Response response = target.request(MediaType.APPLICATION_JSON).post(null);

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("id", null), equalTo("mycart"));
		assertThat(value.getDouble("cartItemTotal", 0), equalTo(new Double(57.5)));

	}

	@Test
	@RunAsClient
	public void testDeleteCart() throws Exception {

		// Mock the Cart service to delete items in a cart
		cartMockRule.stubFor(delete(urlMatching("/cart/[a-zA-Z]+/[0-9]+/[0-9]+"))
		        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
		        .withBodyFile("mycart-item-deleted.json")));

		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/mycart")
				.path("/165614").path("/1");
		Response response = target.request(MediaType.APPLICATION_JSON).delete();

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("id", null), equalTo("mycart"));
		assertThat(value.getDouble("cartItemTotal", 0), equalTo(new Double(28.75)));

		JsonArray items = value.get("shoppingCartItemList").asArray();

		assertThat(items.get(0).asObject().getInt("quantity", 0), equalTo(new Integer(1)));

	}

	@Test
	@RunAsClient
	public void testCheckout() throws Exception {

		// Mock the Cart service to checkout a cart
		cartMockRule.stubFor(post(urlMatching("/cart/checkout/[a-zA-Z]+"))
		        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
		        .withBodyFile("mycart-empty.json")));

		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/checkout")
				.path("/mycart");
		Response response = target.request(MediaType.APPLICATION_JSON).post(null);

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("id", null), equalTo("mycart"));
		assertThat(value.getDouble("cartItemTotal", 0), equalTo(new Double(0.0)));

	}

}
