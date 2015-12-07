package org.restler;

import org.restler.client.CallEnhancer;
import org.restler.http.security.SecuritySession;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * RestlerConfig is used to pass configuration to {@code org.restler.client.CoreModule} implementations
 */
public class RestlerConfig {

    private final URI baseUri;
    private final List<CallEnhancer> enhancers;
    private final Executor restlerThreadPool;
    private final SecuritySession securitySession;

    public RestlerConfig(URI baseUri, List<CallEnhancer> enhancers, Executor restlerThreadPool, SecuritySession securitySession) {
        this.baseUri = baseUri;
        this.enhancers = enhancers;
        this.restlerThreadPool = restlerThreadPool;
        this.securitySession = securitySession;
    }

    public URI getBaseUri() {
        return baseUri;
    }

    public List<CallEnhancer> getEnhancers() {
        return enhancers;
    }

    public Executor getRestlerThreadPool() {
        return restlerThreadPool;
    }

    public SecuritySession getSecuritySession() {
        return securitySession;
    }
}