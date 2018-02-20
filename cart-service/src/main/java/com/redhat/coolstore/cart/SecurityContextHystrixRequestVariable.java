package com.redhat.coolstore.cart;

import org.springframework.security.core.context.SecurityContext;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

public class SecurityContextHystrixRequestVariable {
    private static final HystrixRequestVariableDefault<SecurityContext> securityContextVariable = new HystrixRequestVariableDefault<>();

    private SecurityContextHystrixRequestVariable() {
    }

    public static HystrixRequestVariableDefault<SecurityContext> getInstance() {
        return securityContextVariable;
    }
}
