package com.redhat.coolstore.gateway.api.tests;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
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
public class ProductGatewayTest {

	@Rule
	public WireMockRule catalogMockRule = new WireMockRule(options().port(7070));

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
	public void getProducts() throws Exception {

		// Mock the Catalog service 
		catalogMockRule.stubFor(get(urlMatching("/products"))
		        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
		        .withBodyFile("products.json")));

		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/products");
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		JsonArray items = Json.parse(response.readEntity(String.class)).asArray();

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		assertThat(items.size(), equalTo(new Integer(8)));
		assertThat(items.get(0).asObject().getString("itemId", ""), equalTo("329299"));
		assertThat(items.get(0).asObject().getString("name", ""), equalTo("Red Fedora"));
	}
	
	@Test
	@RunAsClient
	public void getProduct() throws Exception {

		// Mock the Catalog service 
		catalogMockRule.stubFor(get(urlMatching("/product/[0-9]+"))
		        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
		        .withBodyFile("product-329299.json")));

		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/product").path("/329299");
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("name", null), equalTo("Red Fedora"));


	}


}
