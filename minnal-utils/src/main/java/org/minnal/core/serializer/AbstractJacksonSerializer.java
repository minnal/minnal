/**
 * 
 */
package org.minnal.core.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractJacksonSerializer extends Serializer {

	private ObjectMapper mapper;

	private static final Logger logger = LoggerFactory.getLogger(AbstractJacksonSerializer.class);

	protected AbstractJacksonSerializer(ObjectMapper mapper) {
		this.mapper = mapper;
		init();
	}
	
	protected void init() {
		mapper.addMixInAnnotations(Object.class, PropertyFilterMixIn.class);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.GETTER, Visibility.PROTECTED_AND_PUBLIC);
		mapper.setVisibility(PropertyAccessor.SETTER, Visibility.PROTECTED_AND_PUBLIC);
		registerModules(mapper);
		mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
		mapper.setPropertyNamingStrategy(getPropertyNamingStrategy());
		SimpleFilterProvider provider = new SimpleFilterProvider();
		provider.addFilter("property_filter", SimpleBeanPropertyFilter.serializeAllExcept(Sets.<String>newHashSet()));
		mapper.setFilters(provider);
	}
	
	protected abstract void registerModules(ObjectMapper mapper);

	@JsonFilter("property_filter")  
	public class PropertyFilterMixIn {} 

	public <T> T deserialize(InputStream stream, Class<T> targetClass) {
		try {
			return mapper.readValue(stream, targetClass);
		} catch (Exception e) {
			throw new SerializationException("Failed while deserializing the buffer to type - " + targetClass, e);
		} finally {
			closeStream(stream);
		}
	}

	@Override
	public <T extends Collection<E>, E> T deserializeCollection(InputStream stream, Class<T> collectionType, Class<E> elementType) {
		JavaType javaType = mapper.getTypeFactory().constructCollectionType(collectionType, elementType);
		try {
			return mapper.readValue(stream, javaType);
		} catch (Exception e) {
			throw new SerializationException("Failed while deserializing the buffer to type - " + javaType, e);
		} finally {
			closeStream(stream);
		}
	}

	/**
	 * @return the mapper
	 */
	protected ObjectMapper getMapper() {
		return mapper;
	}

	@JsonIgnore
	public PropertyNamingStrategy getPropertyNamingStrategy() {
		return PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES;
	}

	@Override
	public void serialize(Object object, OutputStream stream) {
		try {
			mapper.writeValue(stream, object);  
		} catch (Exception e) {
			logger.error("Unable to serialize object", e);
			throw new SerializationException("Failed while serializing the object", e);
		} finally {
			closeStream(stream);
		}
	}
}
