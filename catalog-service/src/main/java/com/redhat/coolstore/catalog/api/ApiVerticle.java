package com.redhat.coolstore.catalog.api;

import java.util.List;

import com.redhat.coolstore.catalog.model.Product;
import com.redhat.coolstore.catalog.verticle.service.CatalogService;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.circuitbreaker.HystrixMetricHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ApiVerticle extends AbstractVerticle {

	private CatalogService catalogService;
	private CircuitBreaker circuitBreaker;

	public ApiVerticle(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		circuitBreaker = CircuitBreaker.create("coolstore-product-circuit-breaker", vertx, new CircuitBreakerOptions()
				.setMaxFailures(3).setTimeout(1000).setFallbackOnFailure(false).setResetTimeout(1000));

		Router router = Router.router(vertx);
		router.get("/products").handler(this::getProducts);
		router.get("/product/:itemId").handler(this::getProduct);
		router.route("/product").handler(BodyHandler.create());
		router.post("/product").handler(this::addProduct);
		router.get("/hystrix.stream").handler(HystrixMetricHandler.create(vertx));
		router.get("/health/readiness").handler(requestHandler -> {
			requestHandler.response().end("OK");
		});
		HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx).register("health", f -> health(f));
		router.get("/health/liveness").handler(healthCheckHandler);

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("catalog.http.port", 8080),
				result -> {
					if (result.succeeded()) {
						startFuture.complete();
					} else {
						startFuture.fail(result.cause());
					}
				});

	}

	private void getProducts(RoutingContext rc) {

		circuitBreaker.<JsonArray>execute(future -> {
			catalogService.getProducts(ar -> {
				if (ar.succeeded()) {
					List<Product> products = ar.result();
					JsonArray json = new JsonArray();
					products.stream().map(p -> p.toJson()).forEach(p -> json.add(p));
					future.complete(json);
				} else {
					future.fail(ar.cause());
				}
			});
		}).setHandler(ar -> {
			if (ar.succeeded()) {
				rc.response().putHeader("Content-type", "application/json").end(ar.result().encodePrettily());
			} else {
				rc.fail(503);
			}
		});
	}

	private void getProduct(RoutingContext rc) {
		String itemId = rc.request().getParam("itemid");
		circuitBreaker.<Product>execute(future -> {
			catalogService.getProduct(itemId, ar -> {
				if (ar.succeeded()) {
					future.complete(ar.result());
				} else {
					future.fail(ar.cause());
				}
			});
		}).setHandler(ar -> {
			if (ar.succeeded()) {
				if (ar.result() != null) {
					// get alternative products
					rc.response().putHeader("Content-type", "application/json")
							.end(ar.result().toJson().encodePrettily());
				} else {
					rc.fail(404);
				}
			} else {
				rc.fail(503);
			}
		});
	}

	private void addProduct(RoutingContext rc) {
		JsonObject json = rc.getBodyAsJson();

		catalogService.addProduct(new Product(json), ar -> {
			if (ar.succeeded()) {
				rc.response().setStatusCode(201).end();
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void health(Future<io.vertx.ext.healthchecks.Status> future) {
		catalogService.ping(ar -> {
			if (ar.succeeded()) {
				if (!future.isComplete()) {
					future.complete(Status.OK());
				} else {
					future.complete(Status.KO());
				}
			}
		});
	}
}
