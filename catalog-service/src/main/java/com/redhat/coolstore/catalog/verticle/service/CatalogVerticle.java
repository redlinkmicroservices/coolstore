package com.redhat.coolstore.catalog.verticle.service;

import java.util.Optional;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ProxyHelper;

public class CatalogVerticle extends AbstractVerticle {
    
    private MongoClient client;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        
        client = MongoClient.createShared(vertx, config());
                
        //----
        // * Create an instance of `CatalogService`.
        CatalogService service = CatalogService.create(getVertx(), config(), client);
        ProxyHelper.registerService(CatalogService.class, getVertx(), service, CatalogService.ADDRESS);
        // * Register the service on the event bus
        startFuture.complete();
        // * Complete the future
        //----
    }

    @Override
    public void stop() throws Exception {
        Optional.ofNullable(client).ifPresent(c -> c.close());
    }

}
