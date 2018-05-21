package com.redhat.coolstore.inventory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
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
        WebTarget target = client.target("http://localhost:" + port).path("/inventory").path("/123456");
        Response response = target.request(MediaType.APPLICATION_JSON).get();

        assertThat(response.getStatus(), equalTo(new Integer(200)));
        JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
        assertThat(value.getString("itemId", null), equalTo("123456"));
        assertThat(value.getString("location", null), equalTo("location"));
        assertThat(value.getInt("quantity", 0), equalTo(new Integer(99)));
        assertThat(value.getString("link", null), equalTo("link"));
    }

    @Test
    @RunAsClient
    public void testGetInventoryWhenItemIdDoesNotExist() throws Exception {
        WebTarget target = client.target("http://localhost:" + port).path("/inventory").path("/doesnotexist");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), equalTo(new Integer(404)));
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
    	WebTarget target = null;
	    // TODO: Invoke the /status URL with the JAX-RS client using a GET request
    	Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), equalTo(new Integer(200)));
        JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
        assertThat(value.getString("id", ""), equalTo("server-state"));
        assertThat(value.getString("result", ""), equalTo("UP"));
    }

    @Test
    @RunAsClient
    public void testError() throws Exception {
        WebTarget target = client.target("http://localhost:" + port).path("/inventory").path("/error");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), equalTo(new Integer(500)));
    }

}
