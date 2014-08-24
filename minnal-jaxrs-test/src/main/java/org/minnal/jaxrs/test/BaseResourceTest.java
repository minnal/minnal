package org.minnal.jaxrs.test;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.Futures;
import org.apache.commons.beanutils.PropertyUtils;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.minnal.autopojo.AutoPojoFactory;
import org.minnal.autopojo.Configuration;
import org.minnal.autopojo.GenerationStrategy;
import org.minnal.autopojo.util.PropertyUtil;
import org.minnal.jaxrs.test.exception.MinnalJaxrsTestException;
import org.minnal.jaxrs.test.provider.JacksonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseResourceTest {

    private static final int MAX_DEPTH = 20;

    private static final Logger logger = LoggerFactory.getLogger(BaseResourceTest.class);

    private static AutoPojoFactory factory;

    static {
        Set<Class<? extends Annotation>> excludeAnnotations = new HashSet<Class<? extends Annotation>>();
        excludeAnnotations.add(JsonIgnore.class);
        excludeAnnotations.add(JsonBackReference.class);
        try {
            excludeAnnotations.add((Class) ClassUtil.findClass("javax.persistence.GeneratedValue"));
        } catch (ClassNotFoundException e) {
            logger.trace("javax.persistence.GeneratedValue class not found. Ignoring it", e);
        }
        Configuration configuration = new Configuration();
        configuration.setExcludeAnnotations(excludeAnnotations);
        GenerationStrategy strategy = new GenerationStrategy(configuration);
        strategy.register(Object.class, BiDirectionalObjectResolver.class);
        strategy.register(Collection.class, BiDirectionalCollectionResolver.class);
        factory = new AutoPojoFactory(strategy);
    }

    private JacksonProvider provider;

    private ApplicationHandler handler;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeSuite
    public void beforeSuite() {
    }

    @BeforeMethod
    public void beforeMethod() {
        setup();
    }

    @AfterMethod
    public void afterMethod() {
        destroy();
    }

    @AfterSuite
    public void afterSuite() {
    }

    protected void init(ResourceConfig resourceConfig) {
        this.provider = new JacksonProvider(MAPPER);
        this.handler = createApplicationHandler(resourceConfig);
    }

    public void setup() {
    }

    public void destroy() {
    }

    public ContainerRequest request(String uri, String method, ByteBuffer content) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ByteStreams.copy(new ByteArrayInputStream(content.array()), bos);
            return request(uri, method, bos.toString(Charsets.UTF_8.name()), MediaType.APPLICATION_JSON_TYPE);
        } catch (Exception e) {
            throw new MinnalJaxrsTestException(e);
        }
    }

    public ContainerRequest request(String uri, String method) {
        return request(uri, method, "", MediaType.APPLICATION_JSON_TYPE);
    }

    public ContainerRequest request(String uri, String method, String content) {
        return request(uri, method, content, MediaType.APPLICATION_JSON_TYPE);
    }

    public ContainerRequest request(String uri, String method, String content, MediaType contentType) {
        return request(uri, method, content, contentType, Maps.<String, String>newHashMap());
    }

    public ContainerRequest request(String uri, String method, String content, MediaType contentType, Map<String, String> headers) {
        return createContainerRequest(URI.create(""), URI.create(uri), method, content, headers,
                null, new MapPropertiesDelegate());
    }

    public <T> T createDomain(Class<T> clazz, Class<?>... genericTypes) {
        return factory.populate(clazz, MAX_DEPTH, genericTypes);
    }

    public <T> T createDomain(Class<T> clazz, int maxDepth, Class<?>... genericTypes) {
        return factory.populate(clazz, maxDepth, genericTypes);
    }

    public <T> boolean compare(T model1, T model2, int depth) {
        if (model1 == null || model2 == null) {
            return false;
        }
        for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(model1)) {
            if (PropertyUtil.isSimpleProperty(descriptor.getPropertyType())) {
                try {
                    Object property1 = PropertyUtils.getProperty(model1, descriptor.getName());
                    Object property2 = PropertyUtils.getProperty(model2, descriptor.getName());
                    if (property1 != null && property2 != null && !property1.equals(property2)) {
                        return false;
                    }
                } catch (Exception e) {
                    logger.info(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    public ByteBuffer serialize(Object value) {
        return provider.serialize(value, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
    }

    public <T> T deserialize(ByteBuffer byteBuf, Class<T> type) {
        return (T) provider.deserialize(byteBuf, type, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
    }

    public <T> T deserializeCollection(ByteBuffer byteBuf, Class<T> type, Class elementType) {
        return (T) provider.deserializeCollection(byteBuf, type, elementType, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
    }

    public ContainerResponse call(ContainerRequest containerRequest) {
        ByteBuffer byteBuf = ByteBuffer.allocate(10240);
        ContainerResponse response;
        try {
            response = Futures.getUnchecked(handler.apply(containerRequest, new ByteBufferOutputStream(byteBuf)));
        } catch (Exception e) {
            logger.debug("Failed while handling the request - " + containerRequest, e);
            response = new ContainerResponse(containerRequest, Response.serverError().build());
        }
        ContainerResponseWriter responseWriter = containerRequest.getResponseWriter();
        OutputStream os = responseWriter.writeResponseStatusAndHeaders(response.getLength(), response);
        response.setEntityStream(os);
        return response;
    }

    private ApplicationHandler createApplicationHandler(ResourceConfig resourceConfig) {
        return new ApplicationHandler(resourceConfig);
    }

    private ContainerRequest createContainerRequest(URI baseUri, URI requestUri, String method, String content, Map<String, String> headers,
                                                    SecurityContext securityContext, PropertiesDelegate propertiesDelegate) {
        URI uri = URI.create(baseUri.getPath() + "/");
        ContainerRequest containerRequest = new ContainerRequest(uri, requestUri, method, securityContext, propertiesDelegate);
        containerRequest.setEntityStream(new ByteArrayInputStream(content.getBytes()));

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            containerRequest.getHeaders().add(headerEntry.getKey(), headerEntry.getValue());
        }
        return containerRequest;
    }
}
