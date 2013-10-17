/**
 * 
 */
package org.minnal.core.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.minnal.core.serializer.Serializer;

import com.google.common.collect.FluentIterable;
import com.google.common.net.MediaType;

/**
 * @author anand.karthik
 *
 */
public class DefaultResponseWriter implements ResponseWriter {
	
	private ServerResponse response;

	/**
	 * @param response
	 */
	public DefaultResponseWriter(ServerResponse response) {
		this.response = response;
	}
	
	public Set<String> getParamsSetFromHeaders(String headerParams){
		if (headerParams == null){
			return null;
		}		
		Set<String> params = new HashSet<String>(Arrays.asList(headerParams.split(",")));		
		return params;
	}

	@Override
	public void write(Object content) {
		MediaType type = null;
		Serializer serializer = null;
		ServerRequest request = response.getRequest();
		if (request.getSupportedAccepts() != null) {
			type = FluentIterable.from(request.getSupportedAccepts()).first().or(response.getResolvedRoute().getConfiguration().getDefaultMediaType());
			serializer = response.getSerializer(type);
		} else {
			type = MediaType.PLAIN_TEXT_UTF_8;
			serializer = Serializer.DEFAULT_TEXT_SERIALIZER;
		}
		response.setContentType(type);
		response.setContent(serializer.serialize(content,getParamsSetFromHeaders(response.getHeader("include")), 
				getParamsSetFromHeaders(response.getHeader("exclude"))));
	}

}
