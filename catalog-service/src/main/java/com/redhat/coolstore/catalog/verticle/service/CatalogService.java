package com.redhat.coolstore.catalog.verticle.service;

import java.util.List;

import com.redhat.coolstore.catalog.model.Product;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
public interface CatalogService {

    final static String ADDRESS = "catalog-service"; 

    static CatalogService create(Vertx vertx, JsonObject config, MongoClient client) {
    	//TODO: instantiate an implementation of the interface
        return null;
    }

    static CatalogService createProxy(Vertx vertx) {
    	//TODO: create the event bus proxy
    	return null;
    }
    
    void getProducts(Handler<AsyncResult<List<Product>>> resulthandler);

    void getProduct(String itemId, Handler<AsyncResult<Product>> resulthandler);

    void addProduct(Product product, Handler<AsyncResult<String>> resulthandler);

    void ping(Handler<AsyncResult<String>> resultHandler);

}
