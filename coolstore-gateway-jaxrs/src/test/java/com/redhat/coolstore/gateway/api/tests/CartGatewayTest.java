package com.redhat.coolstore.gateway.api.tests;

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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.redhat.coolstore.gateway.api.RestApplication;
import com.redhat.coolstore.gateway.model.ShoppingCartItem;
import com.redhat.coolstore.gateway.proxy.CartResource;

@Category(UnitTests.class)
@RunWith(Arquillian.class)
public class CartGatewayTest {

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
				.addAsWebInfResource("test-beans.xml", "beans.xml")
				.addAsResource("project-local.yml", "project-local.yml")
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
		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/mycart");
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("id", null), equalTo("mycart"));

	}

	@Test
	@RunAsClient
	public void testAddCart() throws Exception {
		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/FOO")
				.path("/329299").path("/2");
		Response response = target.request(MediaType.APPLICATION_JSON).post(null);

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("id", null), equalTo("FOO"));
		assertThat(value.getDouble("cartItemTotal", 0), equalTo(new Double(69.98)));

	}

	@Test
	@RunAsClient
	public void testDeleteCart() throws Exception {
		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/FOO")
				.path("/329299").path("/2");
		Response response = target.request(MediaType.APPLICATION_JSON).delete();

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("id", null), equalTo("FOO"));
		assertThat(value.getInt("quantity", 0), equalTo(new Integer(0)));

	}

	@Test
	@RunAsClient
	public void testCheckout() throws Exception {
		WebTarget target = client.target("http://localhost:" + port).path("/api").path("/cart/").path("/checkout")
				.path("/FOO");
		Response response = target.request(MediaType.APPLICATION_JSON).post(null);

		assertThat(response.getStatus(), equalTo(new Integer(200)));
		JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
		assertThat(value.getString("id", null), equalTo("FOO"));
		assertThat(value.getDouble("cartItemTotal", 0), equalTo(new Double(0.0)));

	}

}
