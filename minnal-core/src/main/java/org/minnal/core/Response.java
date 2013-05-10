/**
 * 
 */
package org.minnal.core;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public interface Response extends Message {

	void setStatus(HttpResponseStatus status);
	
	HttpResponseStatus getStatus();
	
	void setContent(Object content);
	
	void setContentType(MediaType type);
}
