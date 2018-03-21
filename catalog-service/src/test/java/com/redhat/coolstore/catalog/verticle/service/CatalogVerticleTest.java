package com.redhat.coolstore.catalog.verticle.service;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class CatalogVerticleTest extends MongoTestBase {

    private Vertx vertx;
    CatalogService proxy;

    @Before
    public void setup(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        JsonObject config = getConfig();
        mongoClient = MongoClient.createNonShared(vertx, config);
        Async async = context.async();
        dropCollection(mongoClient, "products", async, context);
        async.await(10000);
        
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
		vertx.deployVerticle(new CatalogVerticle(), options, context.asyncAssertSuccess());
		proxy = CatalogService.createProxy(vertx);
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        mongoClient.close();
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testGetProducts(TestContext context) throws Exception {
    	Async saveAsync = context.async(2);
        String itemId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("itemId", itemId1)
                .put("name", "productName1")
                .put("desc", "productDescription1")
                .put("price", new Double(100.0));
    	mongoClient.save("products", json1 , ar -> {
    	    if (ar.failed()) {
    	        context.fail();
    	    }
    	    saveAsync.countDown();
    	});
        String itemId2 = "222222";
        JsonObject json2 = new JsonObject()
                .put("itemId", itemId2)
                .put("name", "productName2")
                .put("desc", "productDescription2")
                .put("price", new Double(100.0));
        mongoClient.save("products", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        saveAsync.await();

        Async async = context.async();
        proxy.getProducts(ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().size(), equalTo(2));
                Set<String> itemIds = ar.result().stream().map(p -> p.getItemId()).collect(Collectors.toSet());
                assertThat(itemIds.size(), equalTo(2));
                assertThat(itemIds, allOf(hasItem(itemId1),hasItem(itemId2)));
                async.complete();
            }
        });
    }

}
