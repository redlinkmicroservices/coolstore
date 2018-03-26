package com.redhat.coolstore.catalog.api;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.redhat.coolstore.catalog.model.Product;
import com.redhat.coolstore.catalog.verticle.service.CatalogService;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ApiVerticleTest {

	private Vertx vertx;
	private Integer port;
	private CatalogService catalogService;
	private ServerSocket socket;

	/**
	 * Before executing our test, let's deploy our verticle.
	 * <p/>
	 * This method instantiates a new Vertx and deploy the verticle. Then, it waits
	 * in the verticle has successfully completed its start sequence (thanks to
	 * `context.asyncAssertSuccess`).
	 *
	 * @param context
	 *            the test context.
	 */
	@Before
	public void setUp(TestContext context) throws IOException {
		vertx = Vertx.vertx();

		// Register the context exception handler
		vertx.exceptionHandler(context.exceptionHandler());

		// Let's configure the verticle to listen on the 'test' port (randomly picked).
		// We create deployment options and set the _configuration_ json object:
		socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();

		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("catalog.http.port", port));

		// Mock the catalog Service
		catalogService = mock(CatalogService.class);

		// We pass the options as the second parameter of the deployVerticle method.
		vertx.deployVerticle(new ApiVerticle(catalogService), options, context.asyncAssertSuccess());
	}

	/**
	 * This method, called after our test, just cleanup everything by closing the
	 * vert.x instance
	 *
	 * @param context
	 *            the test context
	 * @throws IOException
	 */
	@After
	public void tearDown(TestContext context) throws IOException {
		vertx.close(context.asyncAssertSuccess());
	}

	@Test
	public void testGetProducts(TestContext context) throws Exception {
		String itemId1 = "111111";
		JsonObject json1 = new JsonObject().put("itemId", itemId1).put("name", "productName1")
				.put("desc", "productDescription1").put("price", new Double(100.0));
		String itemId2 = "222222";
		JsonObject json2 = new JsonObject().put("itemId", itemId2).put("name", "productName2")
				.put("desc", "productDescription2").put("price", new Double(100.0));
		List<Product> products = new ArrayList<>();
		products.add(new Product(json1));
		products.add(new Product(json2));
		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				Handler<AsyncResult<List<Product>>> handler = invocation.getArgument(0);
				handler.handle(Future.succeededFuture(products));
				return null;
			}
		}).when(catalogService).getProducts(any());

		Async async = context.async();
		vertx.createHttpClient().get(port, "localhost", "/products", response -> {
			assertThat(response.statusCode(), equalTo(200));
			assertThat(response.headers().get("Content-Type"), equalTo("application/json"));
			response.bodyHandler(body -> {
				JsonArray json = body.toJsonArray();
				Set<String> itemIds = json.stream().map(j -> new Product((JsonObject) j)).map(p -> p.getItemId())
						.collect(Collectors.toSet());
				assertThat(itemIds.size(), equalTo(2));
				assertThat(itemIds, allOf(hasItem(itemId1), hasItem(itemId2)));
				verify(catalogService).getProducts(any());
				async.complete();
			}).exceptionHandler(context.exceptionHandler());
		}).exceptionHandler(context.exceptionHandler()).end();
	}

	@Test
	public void testGetProduct(TestContext context) throws Exception {
		String itemId1 = "111111";
		JsonObject json1 = new JsonObject().put("itemId", itemId1).put("name", "productName1")
				.put("desc", "productDescription1").put("price", new Double(100.0));
		Product product = new Product(json1);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                /*
                 * TODO check the mocked call arguments
                 * Return the product from the mocked call
                 */
                return null;
             }
         }).when(catalogService).getProduct(any(),any());

		Async async = context.async();
		/*
		 * TODO use HttpClient to invoke the '/product/{itemId}' endpoint
		 * Assert that the return status code is 200
		 * Assert that the 'Content-Type' header indicates JSON content
		 * Get the response body as a JsonObject
		 * Assert that the JsonObject can be converted into a Product object
		 * Assert that the returned product has the correct id
		 * Assert that the returned product has the correct price
		 * Verify that the mocked CatalogService.getProduct method was called
		 * Do not forget to call the TestContext exceptionHandler method 
		 * and wait for the TestContext to complete async calls
		 */
		async.complete();
		fail("Not implemented yet");
	}

    @Test
    public void testGetNonExistingProduct(TestContext context) throws Exception {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<Product>> handler = invocation.getArgument(1);
                handler.handle(Future.succeededFuture(null));
                return null;
             }
         }).when(catalogService).getProduct(eq("111111"),any());

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/product/111111", response -> {
                assertThat(response.statusCode(), equalTo(404));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }

	@Test
    public void testAddProduct(TestContext context) throws Exception {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<String>> handler = invocation.getArgument(1);
                handler.handle(Future.succeededFuture(null));
                return null;
             }
         }).when(catalogService).addProduct(any(),any());

        Async async = context.async();
        String itemId = "111111";
        JsonObject json = new JsonObject()
                .put("itemId", itemId)
                .put("name", "productName")
                .put("desc", "productDescription")
                .put("price", new Double(100.0));
        String body = json.encodePrettily();
        vertx.createHttpClient().post(port, "localhost", "/product")
            .putHeader("Content-Type", "application/json")
            .handler(response -> {
                assertThat(response.statusCode(), equalTo(201));
                ArgumentCaptor<Product> argument = ArgumentCaptor.forClass(Product.class);
                verify(catalogService).addProduct(argument.capture(), any());
                assertThat(argument.getValue().getItemId(), equalTo(itemId));
                async.complete();
            })
            .end(body);
    }

	@Test
    public void testLiveness(TestContext context) throws Exception {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<String>> handler = invocation.getArgument(0);
                handler.handle(Future.succeededFuture("ok"));
                return null;
             }
        }).when(catalogService).ping(any());
        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/health/liveness", response -> {
            assertThat(response.statusCode(), equalTo(200));
            response.bodyHandler(body -> {
                JsonObject json = body.toJsonObject();
                assertThat(json.toString(), containsString("\"outcome\":\"UP\""));
                async.complete();
            }).exceptionHandler(context.exceptionHandler());
        })
        .exceptionHandler(context.exceptionHandler())
        .end();
    }

	@Test
    public void testHealthReadiness(TestContext context) throws Exception {
	    Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/health/readiness", response -> {
                assertThat(response.statusCode(), equalTo(200));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
        }

	
}
