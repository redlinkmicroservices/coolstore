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
		deployVerticles(new JsonObject(), startFuture);
	}
	
//	@Override
//	public void start(Future<Void> startFuture) throws Exception {
//
//		ConfigStoreOptions appStore = new ConfigStoreOptions();
//		//TODO: configure the ConfigStore to use an OpenShift configmap
//		
//		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
//		configRetrieverOptions.addStore(appStore);
//		
//		ConfigRetriever retriever = ConfigRetriever.create(vertx, configRetrieverOptions);
//		retriever.getConfig(handler -> {
//			if (handler.succeeded()) {
//				deployVerticles(handler.result(), startFuture);
//			}
//			else {
//				startFuture.fail(handler.cause());
//			}
//		});
//	}

	private void deployVerticles(JsonObject config, Future<Void> startFuture) {
		CatalogService proxy = CatalogService.createProxy(vertx);
		
		DeploymentOptions options = new DeploymentOptions();
		options.setConfig(config);
		ApiVerticle apiVerticle = new ApiVerticle(proxy);
		Future<String> apiVerticleFuture = Future.future();
		vertx.deployVerticle(apiVerticle, options, apiVerticleFuture.completer());

		CatalogVerticle catalogVerticle = new CatalogVerticle();
		Future<String> catalogVerticleFuture = Future.future();
		vertx.deployVerticle(catalogVerticle, options, catalogVerticleFuture.completer());

		CompositeFuture.all(apiVerticleFuture, catalogVerticleFuture).setHandler(handler -> {
			if (handler.succeeded()) {
				startFuture.complete();
			}
			else {
				startFuture.fail(handler.cause());
			}
		});
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
