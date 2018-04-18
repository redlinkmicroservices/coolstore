package com.redhat.coolstore.inventory.service;

import static org.junit.Assert.fail;

import java.util.Properties;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import com.redhat.coolstore.inventory.model.Inventory;
import com.redhat.coolstore.inventory.tests.UnitTests;

@Category(UnitTests.class)
@RunWith(Arquillian.class)
public class InventoryServiceTest {

    private static String port = System.getProperty("arquillian.swarm.http.port", "18080");

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        Properties properties = new Properties();
        properties.put("swarm.http.port", port);
        return new Swarm(properties).withProfile("local");
    }

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(false, InventoryService.class.getPackage())
                .addPackages(true, Inventory.class.getPackage())
                .addClass(UnitTests.class)
                .addAsResource("project-local.yml", "project-local.yml")
                .addAsResource("META-INF/test-persistence.xml",  "META-INF/persistence.xml")
                .addAsResource("META-INF/test-load.sql",  "META-INF/test-load.sql");
    }

    // TODO: Inject the Inventory service POJO

    @Test
    public void getInventory() throws Exception {
        // TODO: Assert that the injected Inventory service object is not null
	// TODO: Call the getInventory() method and get the inventory object for item id 123456 
	// TODO: Assert that the inventory object returned is not null
	// TODO: Assert that the quantity is 99
	fail("Not implemented yet");
    }

    @Test
    public void getNonExistingInventory() throws Exception {
        // TODO: Assert that the injected Inventory service object is not null
	// TODO: Call the getInventory() method and get the inventory object for an invalid item id called "notfound"
	// TODO: Assert that the returned inventory object is null
	    
	fail("Not implemented yet");
    }
}

