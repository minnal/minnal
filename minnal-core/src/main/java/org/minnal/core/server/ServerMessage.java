/**
 * 
 */
package org.minnal.core.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.minnal.core.Message;
import org.minnal.core.route.Route;
import org.minnal.core.serializer.Serializer;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public abstract class ServerMessage implements Message {

	private HttpMessage message;
	
	private Route resolvedRoute;
	
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
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
	
	public List<String> getHeaders(String name) {
		return message.getHeaders(name);
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
	
	public long getContentLength() {
		return HttpHeaders.getContentLength(message);
	}
	
	public boolean hasContent() {
		return getContentLength() > 0;
	}

	public void setContent(ChannelBuffer content) {
		message.setContent(content);
	}
	
	/**
	 * @return the resolvedRoute
	 */
	public Route getResolvedRoute() {
		return resolvedRoute;
	}

	/**
	 * @param resolvedRoute the resolvedRoute to set
	 */
	public void setResolvedRoute(Route resolvedRoute) {
		this.resolvedRoute = resolvedRoute;
	}
	
	protected Serializer getSerializer(MediaType type) {
		return resolvedRoute.getConfiguration().getSerializer(type);
	}

	public String getCookie(String name) {
		return getCookies().get(name);
	}
	
	protected abstract String getCookieHeaderName();
	
	public Map<String, String> getCookies() {
		String cookies = getHeader(getCookieHeaderName());
		if (Strings.isNullOrEmpty(cookies)) {
			return Maps.newHashMap();
		}
		return Splitter.on(";").omitEmptyStrings().trimResults().withKeyValueSeparator("=").split(cookies);
	}
	
	public void addCookies(Map<String, String> cookies) {
		addHeader(getCookieHeaderName(), Joiner.on(";").withKeyValueSeparator("=").join(cookies));
	}
	
	@Override
	public <T> T getAttribute(String name) {
		return (T) attributes.get(name);
	}
	
	@Override
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	@Override
	public boolean containsAttribute(String name) {
		return attributes.containsKey(name);
	}
}
