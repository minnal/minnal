package org.minnal.jaxrs.test.baseTest;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.minnal.autopojo.AutoPojoFactory;
import org.minnal.autopojo.Configuration;
import org.minnal.autopojo.GenerationStrategy;
import org.minnal.autopojo.util.PropertyUtil;
import org.minnal.core.JacksonProvider;
import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import javax.ws.rs.core.MediaType;
import javax.xml.ws.handler.MessageContext;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
        strategy.register(Collection.class, BiDirectionalObjectResolver.class);
        factory = new AutoPojoFactory(strategy);
    }

//    private Router router;

    protected JacksonProvider provider;

//    private static Container container = new Container();

    @BeforeSuite
    public void beforeSuite() {
//        container.init();
//        container.start();
    }

    @BeforeMethod
    public void beforeMethod() {
//        router = container.getRouter();
//        Application<ApplicationConfiguration> application = container.getApplications().iterator().next();
//        provider = new JacksonProvider(application.getObjectMapper());
        setup();
    }

    @AfterMethod
    public void afterMethod() {
        destroy();
    }

    @AfterSuite
    public void afterSuite() {
//        container.stop();
    }

    protected void setup() {
    }

    protected void destroy() {
    }

    protected void route(MessageContext context) {
//        router.route(context);
    }

    protected FullHttpRequest request(String uri, HttpMethod method, ByteBuf content) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ByteStreams.copy(new ByteBufInputStream(content), bos);
            return request(uri, method, bos.toString(Charsets.UTF_8.name()), MediaType.APPLICATION_JSON_TYPE);
        } catch (Exception e) {
            throw new MinnalException(e);
        }
    }

    protected FullHttpRequest request(String uri, HttpMethod method) {
        return request(uri, method, "", MediaType.APPLICATION_JSON_TYPE);
    }

    protected FullHttpRequest request(String uri, HttpMethod method, String content) {
        return request(uri, method, content, MediaType.APPLICATION_JSON_TYPE);
    }

    protected FullHttpRequest request(String uri, HttpMethod method, String content, MediaType contentType) {
        return request(uri, method, content, contentType, Maps.<String, String>newHashMap());
    }

    protected FullHttpRequest request(String uri, HttpMethod method, String content, MediaType contentType, Map<String, String> headers) {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, uri);
        request.content().writeBytes(buffer(content));
        request.headers().add(HttpHeaders.Names.CONTENT_TYPE, contentType.toString());
        request.headers().add(HttpHeaders.Names.CONTENT_LENGTH, content.length());
        request.headers().add(HttpHeaders.Names.ACCEPT, MediaType.WILDCARD);
        return request;
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
            } catch (IOException e) {
            }
        }
        return buffer;
    }

//    protected FullHttpResponse call(FullHttpRequest request) {
//        MessageContext context = new MessageContext(request, URI.create(""));
//        route(context);
//        return context.getResponse();
//    }

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
                    if (property1 != null && property2 != null && ! property1.equals(property2)) {
                        return  false;
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
}