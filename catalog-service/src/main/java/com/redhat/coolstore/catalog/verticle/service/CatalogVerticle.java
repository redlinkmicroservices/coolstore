package com.redhat.coolstore.catalog.verticle.service;

import java.util.Optional;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.mongo.MongoClient;

public class CatalogVerticle extends AbstractVerticle {
    
    private MongoClient client;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        
        client = MongoClient.createShared(vertx, config());
        
        //TODO: create an instance CatalogService and register to the event bus
        
        startFuture.complete();
    }

    @Override
    public void stop() throws Exception {
        Optional.ofNullable(client).ifPresent(c -> c.close());
    }

}
