/**
 * 
 */
package org.minnal.core.serializer;

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
	
	public abstract ChannelBuffer serialize(Object object);
	
	public abstract <T> T deserialize(ChannelBuffer buffer, Class<T> targetClass);
	
	public static Serializer getSerializer(MediaType mediaType) {
		if (mediaType.equals(MediaType.JSON_UTF_8)) {
			return DEFAULT_JSON_SERIALIZER;
		}
		if (mediaType.equals(MediaType.XML_UTF_8)) {
			return DEFAULT_XML_SERIALIZER;
		}
		if (mediaType.equals(MediaType.PLAIN_TEXT_UTF_8)) {
			return DEFAULT_TEXT_SERIALIZER;
		}
		return null;
	}
	
}