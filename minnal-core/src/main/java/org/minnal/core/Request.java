/**
 * 
 */
package org.minnal.core;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public interface Request extends Message {
	
	URI getUri();
	
	String getRelativePath();
	
	HttpMethod getHttpMethod();
	
	MediaType getContentType();
	
	Set<MediaType> getAccepts();
	
	<T> T getContentAs(Class<T> clazz);
	
	<T extends Collection<E>, E> T getContentAs(Class<T> collectionType, Class<E> elementType);
	
}
