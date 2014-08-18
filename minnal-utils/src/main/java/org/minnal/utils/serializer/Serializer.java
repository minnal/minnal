/**
 * 
 */
package org.minnal.utils.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.common.base.Charsets;

/**
 * @author ganeshs
 *
 */
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
public abstract class Serializer {
	
	private static final Logger logger = LoggerFactory.getLogger(Serializer.class); 
	
	public static final Serializer DEFAULT_JSON_SERIALIZER = new DefaultJsonSerializer();
	
	public static final Serializer DEFAULT_XML_SERIALIZER = new DefaultXmlSerializer();
	
	public static final Serializer DEFAULT_TEXT_SERIALIZER = new DefaultTextSerializer();
	
	public static final Serializer DEFAULT_FORM_SERIALIZER = new DefaultFormSerializer();
	
	public static final Serializer DEFAULT_YAML_SERIALIZER = new DefaultYamlSerializer();
	
	public String serialize(Object object) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		serialize(object, stream);
		return new String(stream.toByteArray(), Charsets.UTF_8);
	}
	
	public abstract void serialize(Object object, OutputStream stream);
	
	public <T> T deserialize(String content, Class<T> targetClass) {
		return deserialize(new ByteArrayInputStream(content.getBytes(Charsets.UTF_8)), targetClass);
	}
	
	public abstract <T> T deserialize(InputStream stream, Class<T> targetClass);
	
	public <T extends Collection<E>, E> T deserializeCollection(String content, Class<T> collectionType, Class<E> elementType) {
		return deserializeCollection(new ByteArrayInputStream(content.getBytes(Charsets.UTF_8)), collectionType, elementType);
	}
	
	public abstract <T extends Collection<E>, E> T deserializeCollection(InputStream stream, Class<T> collectionType, Class<E> elementType);
	
	/**
	 * @param stream
	 */
	protected void closeStream(Closeable stream) {
		try {
			stream.close();
		} catch (Exception e) {
			logger.trace("Failed while closing the stream", e);
		}
	}
}