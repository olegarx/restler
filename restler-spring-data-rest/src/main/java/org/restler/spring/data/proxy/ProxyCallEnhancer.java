package org.restler.spring.data.proxy;

import org.restler.client.Call;
import org.restler.client.CallEnhancer;
import org.restler.client.CallExecutionChain;
import org.restler.client.CallExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ProxyCallEnhancer implements CallEnhancer {
    @Override
    public Object apply(Call call, CallExecutor callExecutor) {
        Object object = callExecutor.execute(call);

        if(object instanceof Object[]) {
            Object[] array = (Object[])object;

            for(Object item : array) {
                initProxyObject(item, callExecutor);
            }
        } else if(object instanceof Collection) {
            Collection<Object> collection = (Collection<Object>)object;

            for(Object item : collection) {
                initProxyObject(item, callExecutor);
            }

        } else {
            initProxyObject(object, callExecutor);
        }


        return object;
    }

    private void initProxyObject(Object object, CallExecutor callExecutor) {
        if(object instanceof ResourceProxy) {
            List<CallEnhancer> proxyEnhancer = new ArrayList<>();
            proxyEnhancer.add(this);
            CallExecutionChain chain = new CallExecutionChain(callExecutor, proxyEnhancer);
            ResourceProxy resourceProxy = (ResourceProxy)object;
            resourceProxy.setExecutor(chain);
        }
    }


}
