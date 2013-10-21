/**
 * 
 */
package org.minnal.core.serializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.minnal.core.MinnalException;
import org.minnal.core.server.AbstractHttpConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public class DefaultJsonSerializer extends Serializer {

	private ObjectMapper mapper;

	private static final Logger logger = LoggerFactory.getLogger(DefaultJsonSerializer.class);

	
	public DefaultJsonSerializer() {
		this(new SimpleModule());
	}

	public DefaultJsonSerializer(ObjectMapper mapper) {
		this(mapper, new SimpleModule());
	}

	public DefaultJsonSerializer(Module module) {
		this(new ObjectMapper(), module);
	}

	protected DefaultJsonSerializer(ObjectMapper mapper, Module module) {
		this.mapper = mapper;
		init(module);
	}

	@JsonFilter("property_filter")  
	public class PropertyFilterMixIn {} 

	protected void init(Module module) {
		mapper.addMixInAnnotations(Object.class, PropertyFilterMixIn.class);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.GETTER, Visibility.PROTECTED_AND_PUBLIC);
		mapper.setVisibility(PropertyAccessor.SETTER, Visibility.PROTECTED_AND_PUBLIC);
		mapper.registerModule(module);
		mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
		mapper.setPropertyNamingStrategy(getPropertyNamingStrategy());
	}



	public ChannelBuffer serialize(Object object) {
		return serialize(object,null,null);
	}

	public <T> T deserialize(ChannelBuffer buffer, Class<T> targetClass) {
		ChannelBufferInputStream is = new ChannelBufferInputStream(buffer);
		try {
			return mapper.readValue(is, targetClass);
		} catch (Exception e) {
			throw new MinnalException("Failed while deserializing the buffer to type - " + targetClass, e);
		}
	}

	@Override
	public <T extends Collection<E>, E> T deserializeCollection(ChannelBuffer buffer, Class<T> collectionType, Class<E> elementType) {
		ChannelBufferInputStream is = new ChannelBufferInputStream(buffer);
		JavaType javaType = mapper.getTypeFactory().constructCollectionType(collectionType, elementType);
		try {
			return mapper.readValue(is, javaType);
		} catch (Exception e) {
			throw new MinnalException("Failed while deserializing the buffer to type - " + javaType, e);
		}
	}

	@JsonIgnore
	public PropertyNamingStrategy getPropertyNamingStrategy() {
		return PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES;
	}

	@Override
	public ChannelBuffer serialize(Object object, Set<String> excludes, Set<String> includes) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferOutputStream os = new ChannelBufferOutputStream(buffer);

		SimpleBeanPropertyFilter filter = null;
		try {
			if (includes != null && includes.size() != 0){
				filter = new SimpleBeanPropertyFilter.FilterExceptFilter(includes);
			} else if (excludes != null && excludes.size() != 0){
				filter = SimpleBeanPropertyFilter.serializeAllExcept(excludes);
			} else{
				filter = SimpleBeanPropertyFilter.serializeAllExcept(new HashSet<String>());;
			}
			FilterProvider filters = new SimpleFilterProvider().addFilter("property_filter", filter);  
			ObjectWriter writer = mapper.writer(filters);  
			writer.writeValue(os, object);
		} catch (Exception e) {
			logger.error("Unable to serialize object to channel buffer with object :{} " +
					"includes : {} and excludes : {}", object.toString(), includes.toString(), excludes.toString());
			throw new MinnalException("Failed while serializing the object", e);
		}
		return buffer;
	}
}
