/**
 * 
 */
package org.minnal.core;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author ganeshs
 *
 */
public interface Response extends Message {

	void setStatus(HttpResponseStatus status);
	
	HttpResponseStatus getStatus();
	
	void setContent(Object content);
}
