/**
 * 
 */
package org.minnal.core;

import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @author ganeshs
 *
 */
public interface Message {

	void addHeader(String name, String value);
	
	void addHeaders(Map<String, String> headers);
	
	String getHeader(String name);
	
	Map<String, String> getHeaders();
	
	Map<String, String> getHeaders(List<String> headerNames);
	
	boolean containsHeader(String name);
	
	ChannelBuffer getContent();
	
	void setContent(ChannelBuffer content);
}
