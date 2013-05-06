/**
 * 
 */
package org.minnal.core.serializer;

import org.jboss.netty.buffer.ChannelBuffer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * @author ganeshs
 *
 */
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
public abstract class Serializer {
	
	public static final Serializer DEFAULT_JSON_SERIALIZER = new DefaultJsonSerializer();
	
	public static final Serializer DEFAULT_XML_SERIALIZER = new DefaultXmlSerializer();
	
	public abstract ChannelBuffer serialize(Object object);
	
	public abstract <T> T deserialize(ChannelBuffer buffer, Class<T> targetClass);
	
}