/**
 * 
 */
package org.minnal.core;

import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.minnal.core.route.Route;

/**
 * @author ganeshs
 *
 */
public interface Message {

	void addHeader(String name, String value);
	
	void addHeaders(Map<String, String> headers);
	
	String getHeader(String name);
	
	List<String> getHeaders(String name);
	
	Map<String, String> getHeaders();
	
	Map<String, String> getHeaders(List<String> headerNames);
	
	boolean containsHeader(String name);
	
	ChannelBuffer getContent();
	
	long getContentLength();
	
	void setContent(ChannelBuffer content);
	
	Route getResolvedRoute();
	
	Map<String, String> getCookies();
	
	String getCookie(String name);
	
	void addCookies(Map<String, String> cookies);
}
