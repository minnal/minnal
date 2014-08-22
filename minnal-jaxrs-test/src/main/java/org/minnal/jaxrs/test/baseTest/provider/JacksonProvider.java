package org.minnal.jaxrs.test.baseTest.provider;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.minnal.jaxrs.test.baseTest.exception.MinnalJaxrsTestException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.io.*;
import java.nio.ByteBuffer;

public class JacksonProvider extends JacksonJaxbJsonProvider {

    /**
     * @param mapper
     */
    public JacksonProvider(ObjectMapper mapper) {
        this(mapper, null);
    }

    /**
     * Default constructor
     */
    public JacksonProvider() {
        this(new ObjectMapper());
    }

    /**
     * @param mapper
     * @param annotationsToUse
     */
    public JacksonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.GETTER, Visibility.PROTECTED_AND_PUBLIC);
        mapper.setVisibility(PropertyAccessor.SETTER, Visibility.PROTECTED_AND_PUBLIC);
        mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        mapper.setPropertyNamingStrategy(getPropertyNamingStrategy());
    }

    /**
     * @return
     */
    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES;
    }

    /**
     * @param object
     */
    public ByteBuffer serialize(Object object, MediaType mediaType) {
        ByteBuffer byteBuf = ByteBuffer.allocate(48);
        OutputStream stream = new ByteArrayOutputStream();
        try {
            writeTo(object, object.getClass(), object.getClass(), null, mediaType, new MultivaluedHashMap<String, Object>(), stream);
        } catch (IOException e) {
            throw new MinnalJaxrsTestException(e);
        }
        return byteBuf;
    }

    public <T> T deserialize(ByteBuffer byteBuf, Class<T> type, MediaType mediaType) {
        InputStream stream = new ByteArrayInputStream(byteBuf.array());
        try {
            return (T) readFrom((Class<Object>) type, type, null, mediaType, new MultivaluedHashMap<String, String>(), stream);
        } catch (IOException e) {
            throw new MinnalJaxrsTestException(e);
        }
    }

    public <T> T deserializeCollection(ByteBuffer byteBuf, Class<T> type, Class<T> genericType, MediaType mediaType) {
        InputStream stream = new ByteArrayInputStream(byteBuf.array());
        try {
            return (T) readFrom((Class<Object>) genericType, (Class<Object>) type, null, mediaType, new MultivaluedHashMap<String, String>(), stream);
        } catch (IOException e) {
            throw new MinnalJaxrsTestException(e);
        }
    }

}
