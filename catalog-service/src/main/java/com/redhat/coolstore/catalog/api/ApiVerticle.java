package com.redhat.coolstore.catalog.api;

import java.util.List;

import com.redhat.coolstore.catalog.model.Product;
import com.redhat.coolstore.catalog.verticle.service.CatalogService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ApiVerticle extends AbstractVerticle {

	private CatalogService catalogService;

	public ApiVerticle(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		Router router = Router.router(vertx);
		//TODO: add routes to the Router

		//TODO: add the router to the HttpServer
		vertx.createHttpServer().listen(config().getInteger("catalog.http.port", 8080),
				result -> {
					if (result.succeeded()) {
						startFuture.complete();
					}
					else {
						startFuture.fail(result.cause());
					}
				});
	}

    private void getProducts(RoutingContext rc) {
    	//TODO: implement this handler 
    }

	private void getProduct(RoutingContext rc) {
        String itemId = rc.request().getParam("itemid");
        catalogService.getProduct(itemId, ar -> {
            if (ar.succeeded()) {
                Product product = ar.result();
                JsonObject json = null;
                if (product != null) {
                    json = product.toJson();
                    rc.response()
		                .putHeader("Content-type", "application/json")
		                .end(json.encodePrettily());
                }
                else {
                    rc.fail(404);
                }
            }
            else {
                rc.fail(ar.cause());
            }
        });
    }

	private void addProduct(RoutingContext rc) {
		//TODO: implement this handler
	}

}
