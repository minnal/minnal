/**
 * 
 */
package org.minnal.core;

import org.jboss.netty.handler.codec.http.HttpMethod;

/**
 * @author ganeshs
 *
 */
public interface Request extends Message {
	
	String getPath();
	
	String getRelativePath();
	
	HttpMethod getHttpMethod();
	
}
