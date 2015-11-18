package com.netflix.hystrix.contrib.javanica.aop;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.ExecutionType;
import com.netflix.hystrix.contrib.javanica.command.MetaHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

import static com.netflix.hystrix.contrib.javanica.utils.AopUtils.getMethodFromTarget;

/**
 * Created by gautham.srinivas on 17/11/15.
 */
public abstract class MetaHolderFactory {
    public MetaHolder create(final ProceedingJoinPoint joinPoint) {
        Method method = getMethodFromTarget(joinPoint);
        Object obj = joinPoint.getTarget();
        Object[] args = joinPoint.getArgs();
        Object proxy = joinPoint.getThis();
        return create(proxy, method, obj, args);
    }

    public MetaHolder create(final MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Object obj = invocation.getThis();
        Object[] args = invocation.getArguments();
        Object proxy = invocation.getThis();
        return create(proxy, method, obj, args);
    }

    public abstract MetaHolder create(Object proxy, Method method, Object obj, Object[] args);

    public static MetaHolder.Builder metaHolderBuilder(Object proxy, Method method, Object obj, Object[] args) {
        return MetaHolder.builder()
                .args(args).method(method).obj(obj).proxyObj(proxy)
                .defaultGroupKey(obj.getClass().getSimpleName());
    }
}
