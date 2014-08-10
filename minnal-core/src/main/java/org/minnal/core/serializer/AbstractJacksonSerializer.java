/**
 * 
 */
package org.minnal.core.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

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
	}
	
	protected abstract void registerModules(ObjectMapper mapper);

	@JsonFilter("property_filter")  
	public class PropertyFilterMixIn {} 

	public ByteBuf serialize(Object object) {
		return serialize(object,null,null);
	}

	public <T> T deserialize(ByteBuf buffer, Class<T> targetClass) {
		ByteBufInputStream is = new ByteBufInputStream(buffer);
		try {
			return mapper.readValue(is, targetClass);
		} catch (Exception e) {
			throw new MinnalException("Failed while deserializing the buffer to type - " + targetClass, e);
		}
	}

	@Override
	public <T extends Collection<E>, E> T deserializeCollection(ByteBuf buffer, Class<T> collectionType, Class<E> elementType) {
		ByteBufInputStream is = new ByteBufInputStream(buffer);
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
	public ByteBuf serialize(Object object, Set<String> excludes, Set<String> includes) {
		ByteBuf buffer = Unpooled.buffer();
		ByteBufOutputStream os = new ByteBufOutputStream(buffer);

		SimpleBeanPropertyFilter filter = null;
		try {
			if (includes != null && !includes.isEmpty()){
				filter = new SimpleBeanPropertyFilter.FilterExceptFilter(includes);
			} else if (excludes != null && !excludes.isEmpty()){
				filter = SimpleBeanPropertyFilter.serializeAllExcept(excludes);
			} else{
				filter = SimpleBeanPropertyFilter.serializeAllExcept(new HashSet<String>());
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
