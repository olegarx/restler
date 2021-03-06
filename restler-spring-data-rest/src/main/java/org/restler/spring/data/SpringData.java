package org.restler.spring.data;

import net.sf.cglib.proxy.InvocationHandler;
import org.restler.client.*;
import org.restler.http.HttpCallExecutor;
import org.restler.http.RequestExecutor;
import org.restler.spring.data.calls.DeleteAllCallEnhancer;
import org.restler.spring.data.calls.SaveSeveralCallEnhancer;
import org.restler.spring.data.util.Repositories;
import org.springframework.data.repository.Repository;

import java.net.URI;
import java.util.List;

public class SpringData extends DefaultCoreModule {

    private final URI baseUrl;
    private final CallExecutionChain chain;

    private final Repositories repositories;

    public SpringData(ClientFactory factory, URI baseUrl, RequestExecutor requestExecutor, List<CallEnhancer> enhancers, List<Class<?>> repositories) {
        super(factory);
        this.baseUrl = baseUrl;

        // this leak
        this.repositories = new Repositories(repositories, this);

        enhancers.add(1, new DeleteAllCallEnhancer(this.repositories));
        enhancers.add(1, new SaveSeveralCallEnhancer(this.repositories));

        HttpCallExecutor callExecutor = new HttpCallExecutor(requestExecutor);
        chain = new CallExecutionChain(callExecutor, enhancers);
    }

    @Override
    public boolean canHandle(ServiceDescriptor descriptor) {
        return descriptor instanceof ClassServiceDescriptor && isRepository(((ClassServiceDescriptor) descriptor).getServiceDescriptor());
    }

    @Override
    protected InvocationHandler createHandler(ServiceDescriptor descriptor) {
        return new CallExecutorInvocationHandler(chain, new SpringDataMethodInvocationMapper(baseUrl, repositories));
    }

    private boolean isRepository(Class<?> someClass) {
        if (someClass.isInterface()) {

            if (someClass == Repository.class) {
                return true;
            }

            Class<?>[] interfaces = someClass.getInterfaces();

            for (Class<?> interf : interfaces) {
                if (isRepository(interf)) {
                    return true;
                }
            }
        }
        return false;
    }
}
