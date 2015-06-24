package org.restler.client;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RepositoryMethodInvocationMapper implements InvocationMapper {

    private final String baseUrl;

    public RepositoryMethodInvocationMapper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public ServiceMethodInvocation<?> apply(Object o, Method method, Object[] args) {
        ServiceMethod<?> description = getDescription(o, method);
        Object requestBody = null;
        Map<String, ?> pathVariables = new HashMap();
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap();

        return new ServiceMethodInvocation<>(baseUrl, description, requestBody, pathVariables, requestParams);
    }

    private ServiceMethod<?> getDescription(Object o, Method method) {

        RepositoryRestResource repositoryAnnotation = o.getClass().getInterfaces()[0].getDeclaredAnnotation(RepositoryRestResource.class);
        RestResource methodAnnotation = method.getDeclaredAnnotation(RestResource.class);

        String repositoryMappedUriString;
        if (repositoryAnnotation == null || repositoryAnnotation.path().isEmpty()) {
            repositoryMappedUriString = getDefaultRepositoryUri(o);
        } else {
            repositoryMappedUriString = repositoryAnnotation.path();
        }

        String methodMappedUriString = "";

        if (methodAnnotation != null) {
            methodMappedUriString = methodAnnotation.path();
        }

        String uriTemplate = UriComponentsBuilder.fromUriString("/").pathSegment(repositoryMappedUriString, methodMappedUriString).build().toUriString();

        Class<?> resultType = method.getReturnType();

        HttpMethod httpMethod = HttpMethod.GET;

        HttpStatus expectedStatus = HttpStatus.OK;

        return new ServiceMethod<>(uriTemplate, resultType, httpMethod, expectedStatus);
    }

    private String getDefaultRepositoryUri(Object o) {
        Type entityType = ((ParameterizedType) o.getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];

        try {
            return Class.forName(entityType.getTypeName()).getSimpleName().toLowerCase() + "s";
        } catch (ClassNotFoundException e) {
            throw new RestlerException("Could not find class for repository's entity type", e);
        }
    }


}
