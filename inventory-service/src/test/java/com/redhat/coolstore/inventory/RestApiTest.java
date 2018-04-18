package com.redhat.coolstore.inventory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

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
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import com.redhat.coolstore.inventory.tests.UnitTests;

@Category(UnitTests.class)
@RunWith(Arquillian.class)
public class RestApiTest {

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

        WebArchive archive =  ShrinkWrap.create(WebArchive.class, "inventory-service.war")
                .addPackages(true, RestApplication.class.getPackage())
                .addClass(ErrorInventoryService.class)
                .addAsWebInfResource("test-beans.xml", "beans.xml")
                .addAsResource("project-local.yml", "project-local.yml")
                .addClass(ErrorInventoryService.class)
                .addAsResource("META-INF/test-persistence.xml",  "META-INF/persistence.xml")
                .addAsResource("META-INF/test-load.sql",  "META-INF/test-load.sql");

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
    public void testGetInventory() throws Exception {
	// TODO: Invoke the path /inventory/{itemId} with the JAX-RS client using a GET request. Use item id of 123456
	
	// TODO: Assert the following on the response object
	//   * HTTP status code of response is 200 OK
	//   * "itemId" is equal to 123456
	//   * "location" is equal to "location"
	//   * "quantity" is equal to "99"
	//   * "link" is equal to "link"
        fail("Not implemented yet");
    }

    @Test
    @RunAsClient
    public void testGetInventoryWhenItemIdDoesNotExist() throws Exception {
	// TODO: Invoke the /inventory/{itemId} end point with the JAX-RS client using a GET request. 
	// Use an invalid item id value that does not exist - for example "doesnotexist"

        // TODO: Assert that the status code of response is 404 - Not Found
        fail("Not implemented yet");
    }

    @Test
    @RunAsClient
    public void testHealthCheckCombined() throws Exception {
        WebTarget target = client.target("http://localhost:" + port).path("/health");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), equalTo(new Integer(200)));
        JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
        assertThat(value.getString("outcome", ""), equalTo("UP"));
        JsonArray checks = value.get("checks").asArray();
        assertThat(checks.size(), equalTo(new Integer(1)));
        JsonObject state = checks.get(0).asObject();
        assertThat(state.getString("id", ""), equalTo("server-state"));
        assertThat(state.getString("result", ""), equalTo("UP"));
    }

    @Test
    @RunAsClient
    public void testHealthCheckStatus() throws Exception {
	// TODO: Invoke the /status URL with the JAX-RS client using a GET request
        
	// TODO: Assert that you get a valid HTTP response - HTTP status code 200 OK

	// TODO: Parse the JSON response and verify the following attribute values:
	//   * "id" should be equal to "server-state"
	//   * "result" should be equal to "UP"
	fail("Not implemented yet");
    }

    @Test
    @RunAsClient
    public void testError() throws Exception {
        WebTarget target = client.target("http://localhost:" + port).path("/inventory").path("/error");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), equalTo(new Integer(500)));
    }

}
