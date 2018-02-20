package com.redhat.coolstore.cart;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.strategy.HystrixPlugins;

@Component
public class HystrixPluginRegistrar implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        HystrixPlugins.getInstance().registerCommandExecutionHook(new SecurityContextRegistrarCommandHook());
    }

}