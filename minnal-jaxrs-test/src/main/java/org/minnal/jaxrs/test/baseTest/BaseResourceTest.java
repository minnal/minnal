package org.minnal.jaxrs.test.baseTest;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.Futures;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.commons.beanutils.PropertyUtils;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.autopojo.AutoPojoFactory;
import org.minnal.autopojo.Configuration;
import org.minnal.autopojo.GenerationStrategy;
import org.minnal.autopojo.util.PropertyUtil;
import org.minnal.core.Application;
import org.minnal.core.Container;
import org.minnal.core.JacksonProvider;
import org.minnal.core.config.ApplicationConfiguration;
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
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
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
        Set<Class<? extends Annotation>> excludeAnnotations = new HashSet<>();
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

    protected JacksonProvider provider;

    private static Container container = new Container();

    private ApplicationHandler handler;
    private Application<ApplicationConfiguration> application;

    @BeforeSuite
    public void beforeSuite() {
        container.init();
        container.start();
    }

    @BeforeMethod
    public void beforeMethod() {
        application = container.getApplications().iterator().next();
        provider = new JacksonProvider(application.getObjectMapper());
        setup();

        handler = createApplicationHandler(application.getResourceConfig());
    }

    @AfterMethod
    public void afterMethod() {
        destroy();
    }

    @AfterSuite
    public void afterSuite() {
        container.stop();
    }

    protected void setup() {
    }

    protected void destroy() {
    }

    protected ContainerRequest request(String uri, HttpMethod method, ByteBuf content) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ByteStreams.copy(new ByteBufInputStream(content), bos);
            return request(uri, method, bos.toString(Charsets.UTF_8.name()), MediaType.APPLICATION_JSON_TYPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ContainerRequest request(String uri, HttpMethod method) {
        return request(uri, method, "", MediaType.APPLICATION_JSON_TYPE);
    }

    protected ContainerRequest request(String uri, HttpMethod method, String content) {
        return request(uri, method, content, MediaType.APPLICATION_JSON_TYPE);
    }

    protected ContainerRequest request(String uri, HttpMethod method, String content, MediaType contentType) {
        return request(uri, method, content, contentType, Maps.<String, String>newHashMap());
    }

    protected ContainerRequest request(String uri, HttpMethod method, String content, MediaType contentType, Map<String, String> headers) {
        return createContainerRequest(URI.create(""), URI.create(uri), method.name(), content, headers,
                null, new MapPropertiesDelegate());
    }

    protected ByteBuf buffer(String content) {
        ByteBuf buffer = Unpooled.buffer();
        ByteBufOutputStream os = new ByteBufOutputStream(buffer);
        try {
            os.write(content.getBytes(Charsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                os.close();
            } catch (IOException ignored) {
            }
        }
        return buffer;
    }

    protected <T> T createDomain(Class<T> clazz, Class<?>... genericTypes) {
        return factory.populate(clazz, MAX_DEPTH, genericTypes);
    }

    protected <T> T createDomain(Class<T> clazz, int maxDepth, Class<?>... genericTypes) {
        return factory.populate(clazz, maxDepth, genericTypes);
    }

    protected <T> boolean compare(T model1, T model2, int depth) {
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

    protected ByteBuf serialize(Object value) {
        return provider.serialize(value, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
    }

    protected <T> T deserialize(ByteBuf byteBuf, Class<T> type) {
        return (T) provider.deserialize(byteBuf, type, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
    }

    protected <T> T deserializeCollection(ByteBuf byteBuf, Class<T> type, Class elementType) {
        return (T) provider.deserializeCollection(byteBuf, type, elementType, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
    }

    protected ContainerResponse call(ContainerRequest containerRequest) {
        ByteBuf buffer = Unpooled.buffer();
        ContainerResponse response;
        try {
            response = Futures.getUnchecked(handler.apply(containerRequest, new ByteBufOutputStream(buffer)));
        } catch (Exception e) {
            logger.debug("Failed while handling the request - " + containerRequest, e);
            response = new ContainerResponse(containerRequest, Response.serverError().build());
        }
        return response;
    }

    private ApplicationHandler createApplicationHandler(ResourceConfig resourceConfig) {
        return new ApplicationHandler(resourceConfig);
    }

    private ContainerRequest createContainerRequest(URI baseUri, URI requestUri, String method, String content, Map<String, String> headers,
                                                    SecurityContext securityContext, PropertiesDelegate propertiesDelegate) {
        URI uri = URI.create(baseUri.resolve(application.getPath()) + "/");
        ContainerRequest containerRequest = new ContainerRequest(uri, requestUri, method, securityContext, propertiesDelegate);
        containerRequest.setEntityStream(new ByteArrayInputStream(content.getBytes()));

        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            containerRequest.getHeaders().add(headerEntry.getKey(), headerEntry.getValue());
        }
        return containerRequest;
    }
}
