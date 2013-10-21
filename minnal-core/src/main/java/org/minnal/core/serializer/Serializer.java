/**
 * 
 */
package org.minnal.core.serializer;

import java.util.Collection;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
public abstract class Serializer {
	
	public static final Serializer DEFAULT_JSON_SERIALIZER = new DefaultJsonSerializer();
	
	public static final Serializer DEFAULT_XML_SERIALIZER = new DefaultXmlSerializer();
	
	public static final Serializer DEFAULT_TEXT_SERIALIZER = new DefaultTextSerializer();
	
	public static final Serializer DEFAULT_FORM_SERIALIZER = new DefaultFormSerializer();
	
	public static final Serializer DEFAULT_YAML_SERIALIZER = new DefaultYamlSerializer();
	
	public abstract ChannelBuffer serialize(Object object);
	
	public ChannelBuffer serialize(Object object, Set<String> excludes, Set<String> includes) {
		throw new UnsupportedOperationException("Not supported by this serializer");
	}
	
	public abstract <T> T deserialize(ChannelBuffer buffer, Class<T> targetClass);
	
	public abstract <T extends Collection<E>, E> T deserializeCollection(ChannelBuffer buffer, Class<T> collectionType, Class<E> elementType);
	
	public static Serializer getSerializer(MediaType mediaType) {
		if (mediaType.is(MediaType.JSON_UTF_8)) {
			return DEFAULT_JSON_SERIALIZER;
		}
		if (mediaType.is(MediaType.XML_UTF_8)) {
			return DEFAULT_XML_SERIALIZER;
		}
		if (mediaType.is(MediaType.PLAIN_TEXT_UTF_8)) {
			return DEFAULT_TEXT_SERIALIZER;
		}
		if (mediaType.is(MediaType.FORM_DATA)) {
			return DEFAULT_FORM_SERIALIZER;
		}
		return null;
	}
	
}