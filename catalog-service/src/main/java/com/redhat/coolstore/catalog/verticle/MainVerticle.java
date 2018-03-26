package com.redhat.coolstore.catalog.verticle;

import com.redhat.coolstore.catalog.api.ApiVerticle;
import com.redhat.coolstore.catalog.verticle.service.CatalogService;
import com.redhat.coolstore.catalog.verticle.service.CatalogVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		// ----
		// To be implemented
		//
		// * Create a `ConfigStoreOptions` instance.
		// * Set the type to "configmap" and the format to "yaml".
		// * Configure the `ConfigStoreOptions` instance with the name and the key of
		// the configmap
		// * Create a `ConfigRetrieverOptions` instance
		// * Add the `ConfigStoreOptions` instance as store to the
		// `ConfigRetrieverOptions` instance
		// * Create a `ConfigRetriever` instance with the `ConfigRetrieverOptions`
		// instance
		// * Use the `ConfigRetriever` instance to retrieve the configuration
		// * If the retrieval was successful, call the `deployVerticles` method,
		// otherwise fail the `startFuture` object.
		//
		// ----
		JsonObject config = new JsonObject();
		deployVerticles(config, startFuture);
	}

	private void deployVerticles(JsonObject config, Future<Void> startFuture) {

		// ----
		// To be implemented
		//
		// * Create a proxy for the `CatalogService`.
		CatalogService proxy = CatalogService.createProxy(vertx);
		
		// * Create an instance of `ApiVerticle` and `CatalogVerticle`
		// * Deploy the verticles
		// * Make sure to pass the verticle configuration object as part of the
		// deployment options
		// * Use `Future` objects to get notified of successful deployment (or failure)
		// of the verticle deployments.
		
		DeploymentOptions options = new DeploymentOptions();
		options.setConfig(config);
		ApiVerticle apiVerticle = new ApiVerticle(proxy);
		Future<String> apiVerticleFuture = Future.future();
		vertx.deployVerticle(apiVerticle, options, apiVerticleFuture.completer());

		CatalogVerticle catalogVerticle = new CatalogVerticle();
		Future<String> catalogVerticleFuture = Future.future();
		vertx.deployVerticle(catalogVerticle, options, catalogVerticleFuture.completer());

		// * Use a `CompositeFuture` to coordinate the deployment of both verticles.
		// * Complete or fail the `startFuture` depending on the result of the
		// CompositeFuture
		//
		// ----
		CompositeFuture.all(apiVerticleFuture, catalogVerticleFuture).setHandler(handler -> {
			if (handler.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(handler.cause());
			}
		});

	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
