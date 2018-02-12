package com.redhat.coolstore.catalog.verticle;

import com.redhat.coolstore.catalog.api.ApiVerticle;
import com.redhat.coolstore.catalog.verticle.service.CatalogService;
import com.redhat.coolstore.catalog.verticle.service.CatalogVerticle;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
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
		ConfigStoreOptions appStore = new ConfigStoreOptions();
		
		

		// * Set the type to "configmap" and the format to "yaml".
		appStore.setType("configmap").setFormat("yaml");
		// * Configure the `ConfigStoreOptions` instance with the name and the key of
		// the configmap
		appStore.setConfig(new JsonObject().put("name", "app-config").put("key", "app-config.yaml"));
		// * Create a `ConfigRetrieverOptions` instance
		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
		// * Add the `ConfigStoreOptions` instance as store to the
		// `ConfigRetrieverOptions` instance
		configRetrieverOptions.addStore(appStore);
		// * Create a `ConfigRetriever` instance with the `ConfigRetrieverOptions`
		// instance
		ConfigRetriever retriever = ConfigRetriever.create(vertx, configRetrieverOptions);
		// * Use the `ConfigRetriever` instance to retrieve the configuration
		retriever.getConfig(handler->{
			// * If the retrieval was successful, call the `deployVerticles` method,
			// otherwise fail the `startFuture` object.
			if(handler.succeeded()) {
				deployVerticles(handler.result(), startFuture);
			}else {
				startFuture.fail(handler.cause());
			}
		});

		//
		// ----
	}

	private void deployVerticles(JsonObject config, Future<Void> startFuture) {

		// ----
		// To be implemented
		//
		// * Create a proxy for the `CatalogService`.
		CatalogService proxy = CatalogService.createProxy(vertx);
		// * Create an instance of `ApiVerticle` and `CatalogVerticle`
		// * Deploy the verticles
		DeploymentOptions options = new DeploymentOptions();
		options.setConfig(config);
		ApiVerticle apiVerticle = new ApiVerticle(proxy);
		Future<String> apiVerticleFuture = Future.future();
		vertx.deployVerticle(apiVerticle, options, apiVerticleFuture.completer());

		CatalogVerticle catalogVerticle = new CatalogVerticle();
		Future<String> catalogVerticleFuture = Future.future();
		vertx.deployVerticle(catalogVerticle, options, catalogVerticleFuture.completer());
		// * Make sure to pass the verticle configuration object as part of the
		// deployment options

		// * Use `Future` objects to get notified of successful deployment (or failure)
		// of the verticle deployments.

		// * Use a `CompositeFuture` to coordinate the deployment of both verticles.
		CompositeFuture.all(apiVerticleFuture, catalogVerticleFuture).setHandler(handler -> {
			if (handler.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(handler.cause());
			}
		});
		// * Complete or fail the `startFuture` depending on the result of the
		// CompositeFuture
		//
		// ----

	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
