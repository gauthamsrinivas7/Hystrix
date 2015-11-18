package com.netflix.hystrix.contrib.javanica.aop;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.ExecutionType;
import com.netflix.hystrix.contrib.javanica.command.MetaHolder;

import java.lang.reflect.Method;
import java.util.List;

import static com.netflix.hystrix.contrib.javanica.utils.AopUtils.getDeclaredMethod;

/**
 * Created by gautham.srinivas on 17/11/15.
 */
public class CollapserMetaHolderFactory extends MetaHolderFactory {
    @Override
    public MetaHolder create(Object proxy, Method collapserMethod, Object obj, Object[] args) {
        HystrixCollapser hystrixCollapser = collapserMethod.getAnnotation(HystrixCollapser.class);
        Method batchCommandMethod = getDeclaredMethod(obj.getClass(), hystrixCollapser.batchMethod(), List.class);
        if (batchCommandMethod == null || !batchCommandMethod.getReturnType().equals(List.class)) {
            throw new IllegalStateException("required batch method for collapser is absent: "
                    + "(java.util.List) " + obj.getClass().getCanonicalName() + "." +
                    hystrixCollapser.batchMethod() + "(java.util.List)");
        }
        HystrixCommand hystrixCommand = batchCommandMethod.getAnnotation(HystrixCommand.class);
        if (hystrixCommand == null) {
            throw new IllegalStateException("batch method must be annotated with HystrixCommand annotation");
        }
        // method of batch hystrix command must be passed to metaholder because basically collapser doesn't have any actions
        // that should be invoked upon intercepted method, its required only for underlying batch command
        MetaHolder.Builder builder =   MetaHolderFactory.metaHolderBuilder(proxy, batchCommandMethod, obj, args);
        builder.hystrixCollapser(hystrixCollapser);
        builder.defaultCollapserKey(collapserMethod.getName());
        builder.collapserExecutionType(ExecutionType.getExecutionType(collapserMethod.getReturnType()));

        builder.defaultCommandKey(batchCommandMethod.getName());
        builder.hystrixCommand(hystrixCommand);
        builder.executionType(ExecutionType.getExecutionType(batchCommandMethod.getReturnType()));
        return builder.build();
    }
}
