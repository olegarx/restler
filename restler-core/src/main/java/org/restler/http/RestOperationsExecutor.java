package org.restler.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.restler.util.Util;
import org.springframework.http.*;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Entity;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RestOperationsExecutor implements Executor {

    private final RestTemplate restTemplate;

    public RestOperationsExecutor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        restTemplate.getMessageConverters().add(0, new RestDataMessageConverter());
        restTemplate.getMessageConverters().add(new BodySavingMessageConverter());
    }

    public <T> ResponseEntity<T> execute(Request<T> executableRequest) {
        RequestEntity<?> requestEntity = executableRequest.toRequestEntity();
        return restTemplate.exchange(requestEntity, executableRequest.getReturnType());
    }

    private class RestDataMessageConverter implements GenericHttpMessageConverter<Object> {

        @Override
        public boolean canRead(Type type, Class<?> aClass, MediaType mediaType) {
            return false;
        }

        @Override
        public Object read(Type type, Class<?> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
            return null;
        }

        @Override
        public boolean canRead(Class<?> aClass, MediaType mediaType) {
            Entity entityAnnotation = aClass.getDeclaredAnnotation(Entity.class);
            return entityAnnotation != null;
        }

        @Override
        public boolean canWrite(Class<?> aClass, MediaType mediaType) {
            return false;
        }

        @Override
        public List<MediaType> getSupportedMediaTypes() {
            List<MediaType> supportedMediaTypes = new ArrayList();
            supportedMediaTypes.add(MediaType.APPLICATION_JSON);
            supportedMediaTypes.add(new MediaType("application/x-spring-data-verbose+json"));
            return supportedMediaTypes;
        }

        @Override
        public Object read(Class<?> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
            ObjectMapper objectMapper = new ObjectMapper();

            String test = Util.toString(httpInputMessage.getBody());

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Object entity = objectMapper.readValue(httpInputMessage.getBody(), aClass);

            return entity;
        }

        @Override
        public void write(Object o, MediaType mediaType, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        }
    }

    private class BodySavingMessageConverter implements GenericHttpMessageConverter<Object> {
        @Override
        public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
            return true;
        }

        @Override
        public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            return throwException(inputMessage);
        }

        @Override
        public boolean canRead(Class<?> clazz, MediaType mediaType) {
            return true;
        }

        @Override
        public boolean canWrite(Class<?> clazz, MediaType mediaType) {
            return true;
        }

        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return new ArrayList<>();
        }

        @Override
        public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            return throwException(inputMessage);
        }

        private Object throwException(HttpInputMessage inputMessage) throws IOException {
            String responseBody = Util.toString(inputMessage.getBody());
            inputMessage.getBody().close();
            throw new HttpExecutionException(responseBody);
        }

        @Override
        public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        }
    }
}
