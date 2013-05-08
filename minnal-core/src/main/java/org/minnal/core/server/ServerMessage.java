/**
 * 
 */
package org.minnal.core.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.minnal.core.Message;

/**
 * @author ganeshs
 *
 */
public abstract class ServerMessage implements Message {

	private HttpMessage message;
	
	public ServerMessage(HttpMessage message) {
		this.message = message;
	}

	public void addHeader(String name, String value) {
		message.addHeader(name, value);
	}

	public void addHeaders(Map<String, String> headers) {
		for (Entry<String, String> entry : headers.entrySet()) {
			message.addHeader(entry.getKey(), entry.getValue());
		}
	}

	public String getHeader(String name) {
		return message.getHeader(name);
	}

	public Map<String, String> getHeaders() {
		return getHeaders(new ArrayList<String>(message.getHeaderNames()));
	}
	
	public Map<String, String> getHeaders(List<String> headerNames) {
		Map<String, String> headers = new HashMap<String, String>();
		if (headerNames == null || headerNames.isEmpty()) {
			return headers;
		}
		for (Entry<String, String> entry : message.getHeaders()) {
			if (headerNames.contains(entry.getKey())) {
				headers.put(entry.getKey(), entry.getValue());
			}
		}
		return headers;
	}

	public boolean containsHeader(String name) {
		return message.containsHeader(name);
	}

	public ChannelBuffer getContent() {
		return message.getContent();
	}

	public void setContent(ChannelBuffer content) {
		message.setContent(content);
	}
}
