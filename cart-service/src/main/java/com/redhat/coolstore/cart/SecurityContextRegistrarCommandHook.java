package com.redhat.coolstore.cart;

import org.springframework.security.core.context.SecurityContextHolder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException.FailureType;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;

public class SecurityContextRegistrarCommandHook extends HystrixCommandExecutionHook {

    @Override
    public <T> void onRunStart(HystrixCommand<T> commandInstance) {
        SecurityContextHolder.setContext(SecurityContextHystrixRequestVariable.getInstance().get());
    }

    @Override
    public <T> T onComplete(HystrixCommand<T> commandInstance, T response) {
        SecurityContextHolder.clearContext();
        return response;
    }

    @Override
    public <T> Exception onError(HystrixCommand<T> commandInstance, FailureType failureType, Exception e) {
        SecurityContextHolder.clearContext();
        return e;
    }

}
